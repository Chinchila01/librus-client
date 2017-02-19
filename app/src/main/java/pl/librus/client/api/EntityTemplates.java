package pl.librus.client.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Map;

import java8.util.function.Supplier;
import pl.librus.client.datamodel.Announcement;
import pl.librus.client.datamodel.Attendance;
import pl.librus.client.datamodel.AttendanceCategory;
import pl.librus.client.datamodel.Average;
import pl.librus.client.datamodel.EmbeddedId;
import pl.librus.client.datamodel.Event;
import pl.librus.client.datamodel.EventCategory;
import pl.librus.client.datamodel.Grade;
import pl.librus.client.datamodel.GradeCategory;
import pl.librus.client.datamodel.GradeComment;
import pl.librus.client.datamodel.HasId;
import pl.librus.client.datamodel.ImmutableAnnouncement;
import pl.librus.client.datamodel.ImmutableAttendance;
import pl.librus.client.datamodel.ImmutableAttendanceCategory;
import pl.librus.client.datamodel.ImmutableAverage;
import pl.librus.client.datamodel.ImmutableEvent;
import pl.librus.client.datamodel.ImmutableEventCategory;
import pl.librus.client.datamodel.ImmutableGrade;
import pl.librus.client.datamodel.ImmutableGradeCategory;
import pl.librus.client.datamodel.ImmutableGradeComment;
import pl.librus.client.datamodel.ImmutableJsonLesson;
import pl.librus.client.datamodel.ImmutableLessonSubject;
import pl.librus.client.datamodel.ImmutableLessonTeacher;
import pl.librus.client.datamodel.ImmutableLibrusAccount;
import pl.librus.client.datamodel.ImmutableLibrusColor;
import pl.librus.client.datamodel.ImmutableLuckyNumber;
import pl.librus.client.datamodel.ImmutableMe;
import pl.librus.client.datamodel.ImmutablePlainLesson;
import pl.librus.client.datamodel.ImmutableSubject;
import pl.librus.client.datamodel.ImmutableTeacher;
import pl.librus.client.datamodel.Lesson;
import pl.librus.client.datamodel.LibrusColor;
import pl.librus.client.datamodel.LuckyNumber;
import pl.librus.client.datamodel.Me;
import pl.librus.client.datamodel.MultipleIds;
import pl.librus.client.datamodel.PlainLesson;
import pl.librus.client.datamodel.Subject;
import pl.librus.client.datamodel.Teacher;

class EntityTemplates {

    private Map<Class<?>, Supplier<?>> templates = ImmutableMap.<Class<?>, Supplier<?>>builder()
            .put(Announcement.class, this::announcement)
            .put(Subject.class, this::subject)
            .put(Teacher.class, this::teacher)
            .put(Grade.class, this::grade)
            .put(GradeCategory.class, this::gradeCategory)
            .put(GradeComment.class, this::gradeComment)
            .put(PlainLesson.class, this::plainLesson)
            .put(Event.class, this::event)
            .put(EventCategory.class, this::eventCategory)
            .put(Attendance.class, this::attendance)
            .put(AttendanceCategory.class, this::attendanceCategory)
            .put(Average.class, this::average)
            .put(LibrusColor.class, this::librusColor)
            .put(LuckyNumber.class, this::luckyNumber)
            .put(Me.class, this::me)
            .put(Lesson.class, this::jsonLesson)
            .build();

    ImmutableLessonSubject lessonSubject() {
        return ImmutableLessonSubject.builder()
                .id("44561")
                .name("Godzina wychowawcza")
                .build();
    }

    ImmutableLessonTeacher lessonTeacher() {
        return ImmutableLessonTeacher.builder()
                .id("1235088")
                .firstName("Tomasz")
                .lastName("Problem")
                .build();
    }

    ImmutableJsonLesson jsonLesson() {
        return ImmutableJsonLesson.builder()
                .cancelled(false)
                .dayNo(1)
                .hourFrom(LocalTime.parse("08:00"))
                .hourTo(LocalTime.parse("08:45"))
                .lessonNo(1)
                .subject(lessonSubject())
                .substitutionClass(true)
                .teacher(lessonTeacher())
                .substitutionClass(true)
                .orgDate(LocalDate.parse("2017-02-06"))
                .orgLessonNo(2)
                .orgLesson(HasId.of("1822337"))
                .orgSubject(HasId.of("44565"))
                .orgTeacher(HasId.of("1235090"))
                .build();
    }

    private Map<Class<?>, IdGenerator> idGenerators = Maps.newHashMap();

    private final String MOCK_ID = "_mock_";

    public ImmutableGrade grade() {
        return ImmutableGrade.builder()
                .id(idFor(Grade.class))
                .date(LocalDate.now())
                .addDate(LocalDateTime.now())
                .addedBy(HasId.of(MOCK_ID))
                .category(HasId.of(MOCK_ID))
                .finalPropositionType(false)
                .finalType(false)
                .grade("4+")
                .lesson(HasId.of(MOCK_ID))
                .semester(1)
                .semesterPropositionType(false)
                .semesterType(false)
                .subject(HasId.of(MOCK_ID))
                .comments(MultipleIds.fromIds(Lists.newArrayList("777", "888")))
                .student(HasId.of("77779"))
                .build();
    }

    public ImmutableSubject subject() {
        return ImmutableSubject.builder()
                .id(idFor(Subject.class))
                .name("Matematyka")
                .build();
    }


    public ImmutableAnnouncement announcement() {
        return ImmutableAnnouncement.builder()
                .id(idFor(Announcement.class))
                .startDate(LocalDate.parse("2016-09-21"))
                .endDate(LocalDate.parse("2017-06-14"))
                .subject("Tytuł ogłoszenia")
                .content("Treść ogłoszenia")
                .addedBy(HasId.of(MOCK_ID))
                .build();
    }

    public ImmutableMe me() {
        return ImmutableMe.of(ImmutableLibrusAccount.builder()
                .email("tompro@gmail.com")
                .firstName("Tomasz")
                .lastName("Problem")
                .login("12222u")
                .build());
    }

    public ImmutableTeacher teacher() {
        return ImmutableTeacher.builder()
                .id(idFor(Teacher.class))
                .firstName("Ala")
                .lastName("Makota")
                .build();
    }

    private ImmutableLuckyNumber luckyNumber() {
        return ImmutableLuckyNumber.builder()
                .luckyNumber(42)
                .day(LocalDate.now())
                .build();
    }

    private ImmutableGradeCategory gradeCategory() {
        return ImmutableGradeCategory.builder()
                .color(HasId.of(MOCK_ID))
                .id(MOCK_ID)
                .name("Mock grade category")
                .weight(5)
                .build();
    }

    private ImmutableGradeComment gradeComment() {
        return ImmutableGradeComment.builder()
                .id(idFor(GradeComment.class))
                .addedBy(HasId.of(MOCK_ID))
                .grade(HasId.of(MOCK_ID))
                .text("Mock comment")
                .build();
    }

    private ImmutablePlainLesson plainLesson() {
        return ImmutablePlainLesson.builder()
                .id(idFor(PlainLesson.class))
                .subject(HasId.of(MOCK_ID))
                .teacher(HasId.of(MOCK_ID))
                .build();
    }

    private ImmutableEvent event() {
        return ImmutableEvent.builder()
                .id(idFor(Event.class))
                .addedBy(HasId.of(MOCK_ID))
                .category(HasId.of(MOCK_ID))
                .content("Mock event")
                .date(LocalDate.now())
                .lessonNo(1)
                .build();
    }

    private ImmutableEventCategory eventCategory() {
        return ImmutableEventCategory.builder()
                .id(idFor(EventCategory.class))
                .name("Mock event category")
                .build();
    }

    private ImmutableAttendance attendance() {
        return ImmutableAttendance.builder()
                .id(idFor(Attendance.class))
                .date(LocalDate.now())
                .addDate(LocalDateTime.now())
                .addedBy(HasId.of(MOCK_ID))
                .lesson(HasId.of(MOCK_ID))
                .lessonNumber(1)
                .type(HasId.of(MOCK_ID))
                .semester(1)
                .build();
    }

    private ImmutableAttendanceCategory attendanceCategory() {
        return ImmutableAttendanceCategory.builder()
                .id(idFor(AttendanceCategory.class))
                .colorRGB("FF0000")
                .name("Mock attendance category")
                .presenceKind(false)
                .priority(1)
                .shortName("mock")
                .standard(true)
                .build();
    }

    public ImmutableAverage average() {
        return ImmutableAverage.builder()
                .fullYear(3.00)
                .semester1(1.00)
                .semester2(2.00)
                .subject(EmbeddedId.create(idFor(Average.class)))
                .build();
    }


    private ImmutableLibrusColor librusColor() {
        return ImmutableLibrusColor.builder()
                .id(idFor(LibrusColor.class))
                .name("supa_black")
                .rawColor("000000")
                .build();
    }

    <T> T forClass(Class<T> clazz) {
        Object instance = templates.get(clazz).get();
        if (instance != null)
            return clazz.cast(instance);
        else
            throw new RuntimeException("Mocking not supported for " + clazz.getSimpleName());
    }

    private String idFor(Class clazz) {
        IdGenerator generator = idGenerators.get(clazz);

        if (generator == null) {
            generator = new IdGenerator(clazz);
            idGenerators.put(clazz, generator);
        }
        return generator.get();
    }

}