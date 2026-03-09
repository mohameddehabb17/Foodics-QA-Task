package com.foodics.qa.api.models;

/**
 * Represents a user payload used by API tests.
 */
public class User {
    private String name;
    private String job;
    private Integer age;

    /**
     * Creates an empty user payload.
     */
    public User() {
    }

    /**
     * Creates a user payload with all supported fields.
     *
     * @param name user name
     * @param job user job title
     * @param age user age
     */
    public User(String name, String job, Integer age) {
        this.name = name;
        this.job = job;
        this.age = age;
    }

    /**
     * Gets user name.
     *
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets user name.
     *
     * @param name user name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets user job.
     *
     * @return user job
     */
    public String getJob() {
        return job;
    }

    /**
     * Sets user job.
     *
     * @param job user job
     */
    public void setJob(String job) {
        this.job = job;
    }

    /**
     * Gets user age.
     *
     * @return user age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets user age.
     *
     * @param age user age
     */
    public void setAge(Integer age) {
        this.age = age;
    }
}

