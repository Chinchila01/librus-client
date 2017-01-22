package pl.librus.client.grades;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;
import eu.davidea.viewholders.ExpandableViewHolder;
import pl.librus.client.LibrusUtils;
import pl.librus.client.R;
import pl.librus.client.api.Subject;

/**
 * Created by szyme on 01.01.2017.
 */

class GradeHeaderItem extends AbstractExpandableHeaderItem<GradeHeaderItem.ViewHolder, GradeEntryItem> implements Comparable<GradeHeaderItem> {

    private static final String TAG = "librus-client-log";
    private Subject subject;
    private int gradeCount = 0;

    GradeHeaderItem(Subject subject) {
        super();
        this.subject = subject;
        setExpanded(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GradeHeaderItem that = (GradeHeaderItem) o;

        return subject.equals(that.subject);

    }

    @Override
    public int hashCode() {
        return subject.hashCode();
    }

    @Override
    public void addSubItem(GradeEntryItem subItem) {
        super.addSubItem(subItem);
        if (subItem instanceof GradeItem)
            gradeCount++;
    }

    GradeHeaderItem sort() {
        if (mSubItems != null) Collections.sort(mSubItems, Collections.reverseOrder());
        return this;
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, ViewHolder holder, int position, List payloads) {
        holder.subject.setText(subject.getName());
        holder.gradeCount.setText(String.valueOf(gradeCount) + ' ' + LibrusUtils.getPluralForm(gradeCount, "ocena", "oceny", "ocen"));
    }

    @Override
    public ViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.grade_category_item, parent, false), adapter);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.grade_category_item;
    }

    @Override
    public int getExpansionLevel() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull GradeHeaderItem o) {
        int countCompare = Boolean.compare(o.gradeCount > 0, gradeCount > 0);
        if (countCompare != 0) return countCompare;
        else return subject.getName().compareTo(o.getSubject().getName());
    }

    public Subject getSubject() {
        return subject;
    }

    class ViewHolder extends ExpandableViewHolder {
        final private TextView subject, gradeCount;

        ViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            gradeCount = (TextView) view.findViewById(R.id.grade_category_item_content);
            subject = (TextView) view.findViewById(R.id.grade_category_item_title);
        }
    }
}
