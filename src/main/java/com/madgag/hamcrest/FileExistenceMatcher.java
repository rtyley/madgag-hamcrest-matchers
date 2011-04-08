package com.madgag.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.io.File;

public class FileExistenceMatcher extends TypeSafeMatcher<File> {

	private final boolean exists;

	private FileExistenceMatcher(boolean exists) {
        this.exists = exists;
    }

	@Override
	public boolean matchesSafely(File file) {
		return file.exists()==exists;
	}

	public void describeTo(Description description) {
		description.appendText("file which ").appendText(exists ? "exists" : "does not exist");
	}

	@Factory
	public static <T> Matcher<File> exists() {
		return new FileExistenceMatcher(true);
	}

    @Factory
	public static <T> Matcher<File> doesNotExist() {
		return new FileExistenceMatcher(false);
	}
}
