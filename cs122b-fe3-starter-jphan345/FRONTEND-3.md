# Frontend 3 - Full

 - [Pages](#pages)
 - [Requirements](#requirements)
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

## Search Page (Previously Implemented)

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


## Movie Detail Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Movie Detail</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
     <td align="left" >This page allows a user to view all the details of a single movie. The user must be allowed to add the movie to their shopping cart with a specified quantity.</td>
    </tr>
  </tbody>
</table>


## Shopping Cart Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Shopping Cart</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates a user viewing the items they have in their cart. Cart items should display (at the very least) the title, quantity, and total price of each movie (quantity * price). The total amount (sum of all costs) must be displayed. Users must be allowed to update the quantity of each item in their cart or remove items entirely. Updating the cart should also update the displayed cart items and prices. This page should provide a link to <code>Checkout Page</code> using Stripe for when the user is ready to finalize their purchase.</td>
    </tr>
  </tbody>
</table>

## Order History Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Order History</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page facilitates a user viewing all of their previous purchases (It will show max 5 as this is what the endpoint will show). Each history entry should display (at the very least) the date and amount paid for a given order.</td>
    </tr>
  </tbody>
</table>

## Checkout Page

<table>
  <thead>
    <tr>
      <td align="left" width="1100">📄&nbsp;&nbsp;Checkout</td>
    </tr>
  </thead>
  <tbody>
    <tr></tr>
    <tr>
      <td align="left" >This page will show the <code>CheckoutForm.jsx</code> provided by Stripe found here: <a href="https://stripe.com/docs/payments/quickstart">Custom Payment Flow</a>. Note that we are using the <b>Custom Payment Flow</b> for this project. Please read through the documentation to see how to set this up. As some guidelines check out the <code>App.jsx</code> file to see how to <code>loadStripe(publicKey)</code> and to see what the work flow looks like (We start by getting our creating our <code>PaymentIntent</code> (by calling our <code>GET /order/payment</code> endpoint) to get our <code>paymentIntentId</code> and <code>clientSecret</code> and then after the payment is complete (by calling <code>retrievePaymentIntent(clientSecret)</code> promise) we call our <code>POST /order/complete</code> endpoint to complete the order.</td>
    </tr>
  </tbody>
</table>

# Requirements

## Images

You must use the images provided by the `backdrop_path` and `poster_path`. You may use this images where ever you have access to them.

# Video Submission

## Create a video submission. 
YouTube private video, shared by link, works well for video submission for this class. Video submissions should be less than 5 minutes long and follow this scenario:
1. Off-screen, deploy your IDM Service, your Movies service, and your mini-frontend on your local device. Do not include this part in the video.
2. Open your browser and open a network panel (`CTRL + SHIFT + J` on Windows, or `COMMAND + OPTION + J` on Mac). Click the `Network` tab so we can see the network activity. Keep this panel open as you continue with the following steps.
3. Navigate to your website.
4. Navigate to the Register View. Register a new user. (user name should be your ucinetid)
5. Login using the user you just created.
6. Go to your cart to show that it is `empty`
7. Go to your order history to show that it is `empty`
8. Search for movies with the genre `Action` with the year `2012` sort by `Rating` `Desc`
9. Make a brand new search for movies with the director `Christopher Nolan` sort by `Rating` `Desc`
10. Find: `The Dark Knight` and go to its detail page.
11. Add `three` copies of `The Dark Knight` to the shopping cart.
12. Search with the director `russo` sort by `Rating` `Desc`
13. Find: `Avengers: Endgame` and go to its detail page.
14. Add `two` copies of `Avengers: Endgame` to the shopping cart.
15. Go to the shopping cart
16. Remove all copies of `Avengers: Endgame` from the cart
17. Update the quantity of `The Dark Knight` to `seven`
18. Go to your checkout page and check out with the test numbers: 424242... (for all slots)
19. Complete the payment
20. Go to your Order History page and show that there is a recent order.

## Submit the video
Submit a link to your video on the corresponding Canvas assignment.
