package testLibs;

//=============================================================================
//Copyright 2006-2013 Daniel W. Dyer
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//=============================================================================

import java.util.Comparator;

import org.testng.ITestResult;

public class TestResultComparator implements Comparator<ITestResult> {
	public int compare(final ITestResult result1, final ITestResult result2) {
		// return result1.getName().compareTo(result2.getName());
		return extractInt(result1.getName()) - extractInt(result2.getName());
	}

	int extractInt(final String str) {
		final String num = str.replaceAll("\\D", "");
		// return 0 if no digits found
		return num.isEmpty() ? 0 : Integer.parseInt(num);
	}
}
