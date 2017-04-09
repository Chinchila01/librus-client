package pl.librus.client.ui.announcements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Ordering;

import java.util.List;

import javax.inject.Inject;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import java8.util.stream.StreamSupport;
import pl.librus.client.MainApplication;
import pl.librus.client.R;
import pl.librus.client.domain.announcement.FullAnnouncement;
import pl.librus.client.presentation.AnnouncementsPresenter;
import pl.librus.client.presentation.MainFragmentPresenter;
import pl.librus.client.ui.MainActivityOps;
import pl.librus.client.ui.MainFragment;

import static java8.util.stream.Collectors.toList;

public class AnnouncementsFragment
        extends MainFragment
        implements AnnouncementsView {

    private View root;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FlexibleAdapter<IFlexible> adapter;
    private final Ordering<AnnouncementItem> ordering = Ordering.natural()
            .onResultOf(AnnouncementItem::getHeaderOrder)
            .compound(Ordering.natural()
                    .onResultOf(AnnouncementItem::getStartDate).reverse());

    @Inject
    AnnouncementsPresenter presenter;

    @Inject
    MainActivityOps mainActivity;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_announcements, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_announcements);
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.fragment_announcements_refresh_layout);

        adapter = new FlexibleAdapter<>(null);
        adapter.setDisplayHeadersAtStartUp(true);
        adapter.mItemClickListener = this::onClick;

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(R.color.md_blue_grey_400, R.color.md_blue_grey_500, R.color.md_blue_grey_600);

        return root;
    }

    @Override
    protected void injectPresenter() {
        MainApplication.getMainActivityComponent()
                .inject(this);
        refreshLayout.setOnRefreshListener(presenter::reload);
        presenter.attachView(this);
    }

    @Override
    protected MainFragmentPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void display(List<? extends FullAnnouncement> announcements) {
        mainActivity.setBackArrow(false);
        List<IFlexible> announcementItems = StreamSupport.stream(announcements)
                .map(a -> new AnnouncementItem(a, AnnouncementUtils.getHeaderOf(a, getContext())))
                .sorted(ordering)
                .collect(toList());

        adapter.clear();
        adapter.addItems(0, announcementItems);
    }

    private boolean onClick(int position) {
        IFlexible item = adapter.getItem(position);
        if (!(item instanceof AnnouncementItem)) return false;

        AnnouncementItem announcementItem = (AnnouncementItem) item;
        presenter.displayDetails(announcementItem);

        return true;
    }

    @Override
    public void displayDetails(AnnouncementItem announcementItem) {
        mainActivity.setBackArrow(true);
        FullAnnouncement announcement = announcementItem.getAnnouncement();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        AnnouncementDetailsFragment announcementDetailsFragment = AnnouncementDetailsFragment.newInstance(announcement);

        TransitionInflater transitionInflater = TransitionInflater.from(getContext());
        Transition details_enter = transitionInflater.inflateTransition(R.transition.details_enter);
        Transition details_exit = transitionInflater.inflateTransition(R.transition.details_exit);

        setSharedElementEnterTransition(details_enter);
        setSharedElementReturnTransition(details_exit);
        setExitTransition(new Fade());
        announcementDetailsFragment.setSharedElementEnterTransition(details_enter);
        announcementDetailsFragment.setSharedElementReturnTransition(details_exit);

        ft.replace(R.id.content_main, announcementDetailsFragment, "Announcement details transition");
        ft.addSharedElement(announcementItem.getBackgroundView(), announcementItem.getBackgroundView().getTransitionName());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void setRefreshing(boolean b) {
        refreshLayout.setRefreshing(b);
    }

}
