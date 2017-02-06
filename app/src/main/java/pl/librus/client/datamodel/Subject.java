package pl.librus.client.datamodel;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;
import org.joda.time.LocalDate;

import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.Key;
import io.requery.Persistable;

@Entity
@Value.Style(builder = "new")
@Value.Immutable
@JsonDeserialize(as=ImmutableSubject.class)
public abstract class Subject implements Persistable {
    @Key
    @Value.Parameter
    public abstract String id();

    @Value.Parameter
    public abstract String name();

    public static class Builder extends ImmutableSubject.Builder {

    }

}