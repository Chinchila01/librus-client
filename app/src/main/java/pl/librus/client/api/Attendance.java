package pl.librus.client.api;

import android.support.annotation.NonNull;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

/**
 * Created by Adam on 13.12.2016.
 */

public class Attendance implements Serializable, Comparable<Attendance> {
    private String id;
    private String lessonId;
    private LocalDate date;
    private LocalDateTime addDate;
    private int lessonNumber;
    private int semesterNumber;
    private String typeId;
    private String addedById;

    public Attendance(String id, String lessonId, LocalDate date, LocalDateTime addDate, int lessonNumber, int semesterNumber, String typeId, String addedById) {
        this.id = id;
        this.lessonId = lessonId;
        this.date = date;
        this.addDate = addDate;
        this.lessonNumber = lessonNumber;
        this.semesterNumber = semesterNumber;
        this.typeId = typeId;
        this.addedById = addedById;
    }

    public String getId() {
        return id;
    }

    public String getLessonId() {
        return lessonId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getAddedById() {
        return addedById;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attendance that = (Attendance) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(@NonNull Attendance o) {
        return date.compareTo(o.getDate());
    }
}
