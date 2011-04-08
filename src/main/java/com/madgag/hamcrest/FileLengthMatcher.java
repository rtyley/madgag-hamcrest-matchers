package com.madgag.hamcrest;

import org.hamcrest.*;

import java.io.File;

import static org.hamcrest.CoreMatchers.equalTo;


public class FileLengthMatcher extends TypeSafeMatcher<File> {

    private final Matcher<Long> lengthMatcher;

    private FileLengthMatcher(Matcher<Long> lengthMatcher) {
        this.lengthMatcher = lengthMatcher;
    }

	@Override
	public boolean matchesSafely(File file) {
		return lengthMatcher.matches(file.length());
	}

	public void describeTo(Description description) {
		description.appendText("file of length ").appendDescriptionOf(lengthMatcher);
	}

    @Factory
	public static <T> Matcher<File> ofLength(long length) {
		return new FileLengthMatcher(equalTo(length));
	}


}
