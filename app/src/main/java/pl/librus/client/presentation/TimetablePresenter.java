package pl.librus.client.presentation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.immutables.value.Value;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import pl.librus.client.MainActivityScope;
import pl.librus.client.R;
import pl.librus.client.data.EntityChange;
import pl.librus.client.data.LibrusData;
import pl.librus.client.data.UpdateHelper;
import pl.librus.client.domain.Identifiable;
import pl.librus.client.domain.LessonRange;
import pl.librus.client.domain.LibrusUnit;
import pl.librus.client.domain.Teacher;
import pl.librus.client.domain.event.FullEvent;
import pl.librus.client.domain.lesson.EnrichedLesson;
import pl.librus.client.domain.lesson.ImmutableEnrichedLesson;
import pl.librus.client.domain.lesson.ImmutableSchoolWeek;
import pl.librus.client.domain.lesson.Lesson;
import pl.librus.client.domain.lesson.SchoolWeek;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.timetable.TimetableFragment;
import pl.librus.client.ui.timetable.TimetableView;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by robwys on 28/03/2017.
 */

@MainActivityScope
public class TimetablePresenter extends ReloadablePresenter<List<SchoolWeek>, TimetableView> {

    private final LibrusData data;

    private LocalDate currentWeekStart;

    private final Timer timer = new Timer("lesson-refresh");

    @Inject
    protected TimetablePresenter(MainActivityOps mainActivity, UpdateHelper updateHelper, LibrusData data, ErrorHandler errorHandler) {
        super(mainActivity, updateHelper, errorHandler);
        this.data = data;
    }

    @Override
    public Fragment getFragment() {
        return new TimetableFragment();
    }

    @Override
    public int getTitle() {
        return R.string.timetable_view_title;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_event_note_black_48dp;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private Single<LessonAdditionalData> getLessonAdditionalData() {
        Single<LibrusUnit> unitSingle = data.findUnit();
        Single<Map<String, FullEvent>> eventSingle = data.findFullEvents().toMap(FullEvent::lessonId);
        return Single.zip(unitSingle, eventSingle, (unit, eventMap) -> ImmutableLessonAdditionalData.of(unit, eventMap));
    }

    @Override
    protected Single<List<SchoolWeek>> fetchData() {
        return getLessonAdditionalData()
                .flatMapObservable(this::findInitialSchoolWeeks)
                .toList();
    }

    private Observable<SchoolWeek> findInitialSchoolWeeks(LessonAdditionalData additionalData) {
        List<LocalDate> initialWeekStarts = resetWeekStart();
        return Observable.fromIterable(initialWeekStarts)
                .concatMapEager(ws -> findSchoolWeek(ws, additionalData)
                        .toObservable());
    }

    private Single<SchoolWeek> findSchoolWeek(LocalDate weekStart, LessonAdditionalData additionalData) {
        return data.findLessonsForWeek(weekStart)
                .map(enrichLesson(additionalData))
                .toList()
                .map(lessons -> ImmutableSchoolWeek.of(weekStart, lessons))
                .cast(SchoolWeek.class)
                .doOnSuccess(o -> incrementCurrentWeek());
    }

    private Function<Lesson, EnrichedLesson> enrichLesson(LessonAdditionalData additionalData) {
        Optional<Integer> currentLessonNo = findCurrentLessonNo(additionalData.unit().lessonRanges());
        LocalDate today = LocalDate.now();
        return lesson -> {
            Optional<FullEvent> event = Optional.fromNullable(additionalData.eventMap().get(lesson.id()));
            return ImmutableEnrichedLesson.builder()
                    .from(lesson)
                    .date(lesson.date())
                    .current(lesson.date().equals(today) &&
                            currentLessonNo.isPresent() &&
                            currentLessonNo.get() == lesson.lessonNo())
                    .event(event)
                    .build();
        };
    }

    @NonNull
    private List<LocalDate> resetWeekStart() {
        currentWeekStart = LocalDate.now().withDayOfWeek(DateTimeConstants.MONDAY);
        return Lists.newArrayList(currentWeekStart, currentWeekStart.plusWeeks(1));
    }

    @Override
    protected void displayData(List<SchoolWeek> data) {
        super.displayData(data);
        view.scrollToDay(LocalDate.now());

        startRefreshTimer();
    }

    private Optional<Integer> findCurrentLessonNo(List<LessonRange> ranges) {
        LocalTime now = LocalTime.now();

        for (int i = 0; i < ranges.size(); i++) {
            LessonRange range = ranges.get(i);
            if (!range.to().isPresent()) {
                continue;
            }
            if (range.to().get().compareTo(now) > 0) {
                return Optional.of(i);
            }
        }
        return Optional.absent();
    }

    public void startRefreshTimer() {
        LocalDate today = LocalDate.now();
        subscription = data.findUnit()
                .map(unit -> Optional.fromNullable(unit.lessonRanges()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ranges -> findCurrentLessonNo(ranges)
                        .transform(ranges::get)
                        .transform(LessonRange::to)
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(refreshTime -> {
                    if (refreshTime.isPresent()) {
                        LibrusUtils.log("Scheduling refresh task to %s", refreshTime.get());
                        Date date = today.toDateTime(refreshTime.get()).toDate();
                        timer.schedule(new RefreshTask(), date);
                    }
                }, errorHandler);
    }

    private class RefreshTask extends TimerTask {
        @Override
        public void run() {
            refresh();
            LibrusUtils.log("Refreshing lessons");
        }

    }

    public void loadMore() {
        subscription = getLessonAdditionalData()
                .flatMap(additionalData -> findSchoolWeek(currentWeekStart, additionalData))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(ifViewAttached(v -> v.setProgress(false)))
                .subscribe(view::displayMore, errorHandler);
    }

    private void incrementCurrentWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
    }

    protected Set<Class<? extends Identifiable>> dependentEntities() {
        return Sets.newHashSet(
                Lesson.class,
                Teacher.class);

    }

    @Override
    protected Observable<? extends EntityChange<? extends Identifiable>> reloadRelevantEntities() {
        return updateHelper.reloadLessons();
    }

    public void lessonClicked(EnrichedLesson lesson) {
        subscription = data.makeFullLesson(lesson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayDetails, errorHandler);
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        timer.purge();
    }

    @Value.Immutable
    public interface LessonAdditionalData {
        @Value.Parameter
        LibrusUnit unit();

        @Value.Parameter
        Map<String, FullEvent> eventMap();
    }
}
