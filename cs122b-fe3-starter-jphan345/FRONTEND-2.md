# Frontend 2 - Search

 - [Pages](#pages)
 - [Video Submission](#video-submission)

# Pages

## Login Page (Already Implemented)

<table>
  <thead>
    <tr>
      <th align="left" width="1100">📄&nbsp;&nbsp;Login</th>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates user login. It should be the first thing a user sees when they first visit your website. Users must be logged in before performing any other actions on your website. If the login process succeeds, the user will be redirected to the home page.</td>
    </tr>
  </tbody>
</table>

## Register Page (Previously Implemented)

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Register</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates user account registration. If the register process succeeds, the user will be redirected to the login page.</td>
    </tr>
  </tbody>
</table>

## Search Page

<table>
  <thead>
    <tr>
      <th colspan="2"  align="left" width="1100">📄&nbsp;&nbsp;Search</th>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td  colspan="2" align="left" >This view allows a logged-in user to search for movies. It should include a search bar, filters, pagination, and a results area. It will make a REST call to the <code>/movies/search</code> endpoint of your Movies microservice. This page view should include at least the following elements:</td>
    </tr>
    <tr>
      <td align="left">Search Bar</td><td align="left" >This area is where users can enter text pertaining to movies they want to view.</td>
    </tr>
    <tr></tr>
    <tr>
      <td align="left" >Filters</td><td align="left" >Users will be able to search for movies by <code>title</code>, <code>year</code>, <code>director</code>, and / or <code>genre</code>. You can have this as separate inputs or a single input that changes depending on some dropdown, whatever you would like.</td>
    </tr>
    <tr></tr>
    <tr>
      <td align="left" rowspan="9">Pagination</td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to select the sort by <code>title</code> (default selection), <code>rating</code>, or <code>year</code></td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to select the order by <code>asc</code> (default selection) or <code>desc</code></td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to select the amount of movies to list <code>10</code> (default selection), <code>25</code>, <code>50</code> or <code>100</code></td> 
    <tr>
    <tr>
      <td align="left" >Users should be able to go to the <code>next</code> or <code>previous</code> page of the search results</td> 
    <tr></tr>
    <tr>
      <td align="left" >Results Area</td> <td align="left" >All results from a search query will be displayed here as a table. The table will have columns corresponding to each movie's title, year, and director. A new search should update these results.</td>
    </tr>
  </tbody>
</table>

# Video Submission

## Create a video submission. 
YouTube private video, shared by link, works well for video submission for this class. Video submissions should be less than 2 minutes long and follow this scenario:
1. Off-screen, deploy your IDM Service, your Movies service, and your mini-frontend on your local device. Do not include this part in the video.
2. Open your browser and open a network panel (`CTRL + SHIFT + J` on Windows, or `COMMAND + OPTION + J` on Mac). Click the `Network` tab so we can see the network activity. Keep this panel open as you continue with the following steps.
3. Navigate to your website. (we should see the Login View with a link to Register Page)
4. Navigate to the Register View. Register a new user. (we should see a switch to the Login Page)
5. Login using the user you just created. (we should see a switch to Home Page)
6. The sort should be `title` with a `asc` order with a limit of `10`
7. Search for movies with directors named `Chris`. If necessary, scroll down to show the results.
6. Search for movies with `game` in their title. If necessary, scroll to show the results.
7. Still using `game` title: switch the sort to `rating`, the order by to `desc` and the limit to `25` and search
8. Still using the same sort, order, and limit: Search for movies released in the year `2005`. If necessary, scroll down to show the results.
9. Go to the next page (page 2) of the previous results.

## Submit the video
Submit a link to your video on the corresponding Canvas assignment.
