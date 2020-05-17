# revolut-task-jaggrat

At the start of the app internet should be connected in order to fetch the data, otherwise internet error message shown to user. This application supports both the orientation. The test case coverage for ViewModel/Business logic is over ~90%.

I have used following tech-stack to develop this application

- [x] MVVM: Used MVVM with ViewModel and LiveData to maintain updated state of UI during configuration change.
- [x] Kotlin: Used Kotlin for business, view, and test cases 100%. 
- [x] RxJava : To fetch the response from API. 
- [x] Retrofit : Called API with query params.
- [x] Dagger : Dependency injector. 
- [x] Mockito : Tested ViewModel since it is handling logic of conversion into data. Used mockito to mock the repository.The code coverage for ViewModel is 85%.
 

Note: If I had more time I could have done the better job in UI. I could have used flags etc.

Test case report: 




