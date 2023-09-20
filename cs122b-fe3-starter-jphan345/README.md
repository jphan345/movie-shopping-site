# CS122B Frontend

- [Setup Instructions](#setup-instructions)
- [React](#react)
- [React Router Dom](#react-router-dom)
- [React Hook Form](#react-hook-form)
- [Axios](#axios)
- [Styled Components](#styled-components)

## Setup Instructions

1. Download and install Node.js: [Javascript Section Tools](https://github.com/klefstad-teaching/CS122B#javascript)
2. Type `npm` in your terminal to ensure you correctly installed node
3. Type `npm install` within the root of this repo to install all the dependencies (This will take a while)
4. Type `npm start` within the root of this repo to start the server and you should have a window open with the front end
5. When running the frontend please make sure if is set to the **default port of 3000** the backend depends on this to properly communicate with it. (Sometimes it will ask you if you would like to run on a new port, always makesure it runs on port 3000)

## React

We will be using `React` to help build our frontend. `React` as the name implies is a library that updates the html to *react* to internal changes in our javascript code.

### Making a React Component

To make a React component all we need to do is have a function that returns some [JSX](https://reactjs.org/docs/introducing-jsx.html).

```javascript
const App = () => {
    return (
        <div>
            <h1>Hello World!</h1>
        </div>
    );
};
```

Notice that once you are inside of a `JSX` block text is treated a literal strings (the 'Hello World!'). If you wanted to place some javascript inside of `JSX` then you would need to wrap it in `{}` braces like so:

```javascript
const App = () => {
    const message = "Hello World!";
    
    return (
        <div>
            <h1>{message}</h1>
        </div>
    );
};
```

### React.useState()

If we want to use variables that will update the page when they are updated we need to use `React.useState()`. This function takes one argument, the "inital state" of the value, and returns two things: a value, and a function to use when you want to update the value.


```javascript
const App = () => {
    const [ message, setMessage ] = React.useState("Hello World");
    
    const update = () => {
        setMessage("Updated Hello World!");
    };
    
    return (
        <div>
            <h1>{message}</h1>
            <button onClick={update}>Update Message!</h1>
        </div>
    );
};
```

### React.useEffect()

If we want to create some inital logic when the component is created we can use `React.useEffect()`. this function takes two arguments, a lambda to call when the componenet is finished rendering, and a "dependency array" which is a list of variables, which list the varaibles that will cause the lamgda to be called again when they are updated.


```javascript
const App = () => {
    const [ message, setMessage ] = React.useState("");
    
     React.useEffect(() => {
        getMessage(searchParams)
            .then(response => setMessage(response.data.message))
    }, [setMessage]);

    
    return (
        <div>
            <h1>{message}</h1>
        </div>
    );
};
```


## React Router Dom
Helps with user navigation throughout the website. \
[Website](https://github.com/remix-run/react-router) [npm](https://www.npmjs.com/package/react-router-dom)

### Creating a Component that switches

We can have a single component that acts as switch changing the component the user sees depending on the url. 

```javascript
const Content = () => {
    return (
        <div>
            <Routes>
                <Route path="/login" element={<Login/>}/>
                <Route path="/" element={<Home/>}/>
            </Routes>
        </div>
    );
}
```

Here we have a `<Routes>` component with muliple `<Route>` componenets inside. Each `<Route>` lists a component and the path that the url must be for that component to be selected.

### Switching the URL

When we want to "switch" the url so that our `<Routes>` table switches components we can use `NavLink`. This will switch the url without the need to refresh the page and also allows us to "accept" variables inside of the component (More on that later).

```javascript
const NavBar = () => {
    return (
        <nav>
            <NavLink to="/">Home</NavLink>
            <NavLink to="/login">Login</NavLink>
        </nav>
    );
}
```

### Getting URL Values inside of a component in Routes

We can also "grab" values from our url inside of a component.

#### Grabbing a path varaible

Lets say we had this in our `<Routes>`

```javascript
const Content = () => {
    return (
        <div>
            <Routes>
                <Route path="/movie/:id" element={<Movie/>}/>
            </Routes>
        </div>
    );
}
```

This would allow us to have the path after `movie` to be a variable and we could grab that value inside of `<Movie/>` by using the `useParams()` function and using [Desructing assignment](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Destructuring_assignment) to pull out the specific param name (this allows us to do multiple params).


```javascript
const Movie = () => {
    const { id } = useParams(); // The value of ":id" in /movie/:id
    
    return (
        <div>
            Movie ID is: {id}
        </div>
    );
}
```

#### Working with query params

When we want grab and set our url query params we can use `useSearchParams()'.

```javascript
const Movie = () => {
    const [ searchParams, setSearchParams ] = useSearchParams(); // We can set and get our query params with this
    
    return (
        <div>
            Movie Title Query is: {searchParams.get("title")} // gets the value of ?title=<value>
        </div>
    );
}
```

### Navigating

Sometimes we will want to programatically move the user to a new page or url. We do this by using `useNavigate()`.

```javascript
const Movie = () => {
    const navigate = useNavigate(); // We can set and get our query params with this
    
    const moveToDetail = () => {
        navigate("/pathToGoTo");
    };
    
    return (
        <div>
            <button onClick={moveToDetail} >Move me!</button>
        </div>
    );
}
```

## React Hook Form
Useful Api for gathering user input in forms. \
[Website](https://react-hook-form.com) [npm](https://www.npmjs.com/package/react-hook-form)

When we want to gather some input from our users we can use `React Hook Form` in combination with [HTML form elements](https://www.w3schools.com/html/html_form_elements.asp).

### Registering Inputs

- [Offical Documentation: Register Fields](https://react-hook-form.com/get-started#Registerfields)

To use `React Hook Form` we simmply call `useForm()` and by using the [Desructing assignment](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Destructuring_assignment) we can "pull out" the specific functions we want to use from the library. In this case:

 - `register` - we use to "register" a form element so we can gather the value inside
 - `getValues` - we use to get the value from a registed element
 - `handleSubmit` - we use to wrap our `submit` function. We do this to allow for validation (more on this later).

```javascript
const MyComponent = () => {
    const {register, getValues, handleSubmit} = useForm();
    
    const submit = () => {
        console.log(getValues("email"));
    };
    
    return (
        <div>
            <input {...register("email")} type={"email"}/>
            <button onClick={handleSubmit(submit)} >Submit!</button>
        </div>
    );
}
```

### Validating Inputs

- [Offical Documentation: Apply Validation](https://react-hook-form.com/get-started#Applyvalidation)
- [Offical Documentation: Handle Errors](https://react-hook-form.com/get-started#Handleerrors)

When we want to validate some input we can pass an additional value to the `register` call stating the requirements of the input. When we call `register` we have been supplying one argument, the "name": `register("email")` if we want to add validation we just pass another object as the second argument: `register("email", { required: true })` notice that we pass a object, this object is defined in the documentation linked above and gives a lot of options on how to validate the input.

Notice that we have an additional varaible we "pulled" from `useForm()` called `formState: { errors }`, this errors object lists all registered form elements *that have errors*, as in, if you have a input registed as `email` and you find that the value `errors.email` is undefined, then there is no error with email, however if there is a value there then there is an error. The offical documentation has more about this and how to see which validation failed in case of multiple validate requirements.

**Note:** For any of this validation to be checked the `handleSubmit` must be called with your `submit` function as a argument. This is how `React Form Hook` will check to see if the inputs are valid before actually calling your `submit` function.

```javascript
const MyComponent = () => {
    const {register, getValues, formState: { errors }, handleSubmit} = useForm();
    
    const submit = () => {
        console.log(getValues("email"));
    };
    
    return (
        <div>
            <input {...register("email", { required: true })} type={"email"}/>
            {errors.email && "Email is Required" }
            <button onClick={handleSubmit(submit)} >Submit!</button>
        </div>
    );
}
```

## Axios
Api to make REST calls to our backends. \
[Website](https://axios-http.com/) [npm](https://www.npmjs.com/package/axios)

When we want to make REST calls to our Backend we can use `Axios`

### Making REST Calls

We are using [JSONPlaceHolder](https://jsonplaceholder.typicode.com/) as an example here). 

To make a simple `GET` request to `https://jsonplaceholder.typicode.com/todos/1` we would just do this: 

```javascript
async function myRequest() {
    const options = {
        method: "GET", // Method type
        baseURL: "https://jsonplaceholder.typicode.com", // Base part of URL
        url: "/todos/1", // Path part of URL
    }

    return Axios.request(options);
}
```

Since the above function is a `async` function the return type will be wrapped in a `promise` meaning that it will be done "asynchronous". When we call the function we must specify the `then(response => {})` and `catch(error => {})`. We do this because calling a function that is a promise will not complete right away, meaning the rest of the code will be executed after calling the `promise` and when the `promise` is done it will automatically call either the `then` or `catch` function (depending on if the response was successfull or not).

Once the promise completes and goes into the `then(response => {})` function we can pull out the "payload" response model by refrencing `response.data` and since all our backends use JSON the value will automatically come back as a object we can use (no need for mapping or converting).

```javascript
myRequest()
    .then(response => console.log(response.data))
    .error(error => console.log(error.response.data))
    
console.log("This will print right away, since the above statement is a promise and will be done asynchronous.")
```

### Making a REST call with a payload

To make a `POST` request with a payload to `https://jsonplaceholder.typicode.com/posts` we would just do this: 

```javascript
async function myRequest() {
    const payLoad = {
        title: 'foo',
        body: 'bar',
        userId: 1,
    };
           
    const options = {
           method: "POST", // Method type
           baseURL: "https://jsonplaceholder.typicode.com", // Base part of URL
           url: "/posts", // Path part of URL,
           data: payLoad
       }

    return Axios.request(options);
}
```

Axios will automaticall take any object and send the request as a JSON.

### Marking a REST call with query params

To make a `GET` request with query params to `https://jsonplaceholder.typicode.com/posts?userId=1&title=qui est esse` we would just do this: 

```javascript
async function myRequest() {
    const queryParams = {
        userId: 1,
        title: "qui est esse"
    }

    const options = {
        method: "GET", // Method type
        baseURL: "https://jsonplaceholder.typicode.com", // Base part of URL
        url: "/posts", // Path part of URL,
        params: queryParams
    }

    return Axios.request(options);
}
```

### Marking a REST call with an authorization header

To make a request that requres an accessToken as a Authorization header we can pass a object that lists the headers and their values to the `headers` parameter in options like so.

```javascript
async function myRequest(accessToken) {
    const options = {
        method: "GET", // Method type
        baseURL: "http://localhost:8080", // Base part of URL
        url: "/secured/path", // Path part of URL,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}
```

## Styled Components
Api for creating reusable css styled html components. \
[Website](https://styled-components.com/) [npm](https://www.npmjs.com/package/styled-components)

### Styling a html element

When we want to add css styling to a element we can simply create a value and use the format:

```javascript
const StyledDiv = styled.div`
    display: flex;
`
```

by calling `styled.<html element>` we can create a component that acts as that element but has css styling built into it.
