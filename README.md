# UsersManagerSliide
Application for managing users by using Go REST API

## Architecture
 - \[x] Clean Architecture with MVVM as presentation layer

## Aceptance criteria

### 1.Displaying list of users
After app is open list of users is displayed (only users from last page of the endpoint)
Each entry contains name, email address and creation time (relative to now)
Loading and error state are welcome

### 2.Adding new user
After + button is clicked pop up dialog is displayed with name and email entries
After confirmation and successful user creation (201 response code) item is added to the list

### 3.Removing existing user
After item long press pop up dialog is displayed with question “Are you sure you want to remove this user?“
After OK is clicked and user is removed (204 response code) item is deleted from the list

## Technical requirements
- \[x] Kotlin with minimum Android SDK version of 21
- \[x] support device rotation
- \[x] Design follow Material design guidelines
- \[x] RxJava
- \[x] Architecture one of MVVM
- \[x] Dependency injection with Dagger 2
- \[x] Unit tests

### Error handling
Added handling for errors such as authorization failed, no internet, validation failed and others.

Note: Before launching app enter `YOUR_API_KEY` to gorest.properties file in the project.

![image](https://user-images.githubusercontent.com/24879552/167031657-ad761c8e-3abc-42e3-b2ad-6afbde7ee5c1.png)

![users_manager](https://user-images.githubusercontent.com/24879552/167034138-ee22e22a-72f0-4b0b-9bbe-a8431feae3da.gif)




