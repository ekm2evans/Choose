package com.pickupgame.evans.choose.Utilities;

public class Term {
    public String url, term;

    public Term() {
    }

    public Term(String url, String term) {
        this.url = url;
        this.term = term;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
