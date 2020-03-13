package com.kimjongwoo.paintapplication;

import com.google.gson.annotations.SerializedName;

class Data {

    @SerializedName("response")
    Response response;
    @SerializedName("content")
    Content content;

    //return action_result
    public static class Response {
        @SerializedName("action_result")
        String result;

        String getResult() {
            return result;
        }
    }

    //return name and age
    public static class Content {
        @SerializedName("name")
        String name;
        @SerializedName("age")
        String age;

        public String getName() {
            return name;
        }

        String getAge() {
            return age;
        }
    }
}
