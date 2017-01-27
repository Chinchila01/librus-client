package pl.librus.client.api;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Adam on 2016-11-06.
 */

public class LuckyNumber implements Serializable {
    private static final long serialVersionUID = -2804430521463559113L;
    private int luckyNumber;
    private LocalDate luckyNumberDay;

    LuckyNumber(JSONObject data) throws JSONException {
        JSONObject luckyNumber = data.getJSONObject("LuckyNumber");
        this.luckyNumber = luckyNumber.getInt("LuckyNumber");
        this.luckyNumberDay = LocalDate.parse(luckyNumber.getString("LuckyNumberDay"));
    }

    public LuckyNumber(int luckyNumber, LocalDate luckyNumberDay) {
        this.luckyNumber = luckyNumber;
        this.luckyNumberDay = luckyNumberDay;
    }

    public int getLuckyNumber() {
        return luckyNumber;
    }

    public LocalDate getLuckyNumberDay() {
        return luckyNumberDay;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LuckyNumber && ((LuckyNumber) obj).getLuckyNumberDay().equals(luckyNumberDay);
    }
}
