Restifizer SDK for Android (Java)
==========

Restifizer - it's a way to significantly simplify creation of full-functional RESTful services, using MongoDB as a database.
And this is SDK that allows you to easily integrate this solution to your Android applications.
  
> Restifizer is available at https://github.com/vedi/restifizer

> For a quick start you can use our full functional seed-project based on MEAN.JS (http://meanjs.org/), it is available at https://github.com/vedi/restifizer-mean.js.

> The SDK is very young, and we continuously work to improve it. Any feedback is appreciated.

## Prerequisites

This SDK depends on Android volley (compile 'com.android.volley:volley:+'). Please, integrate it to your project.

## Getting started

1. Add sources of SDK to your project.
1. Create singleton to use RestifizerManager without all time setting base url
1. Optionally, set your custom `Error Handler` (see `Error Handling` for details).
1. Init your RestifizerManager in singleton and start use Restifizer

You're ready to make requests to Restifizer.

## Requests

RestifizerManager allows you to create `RestifizerRequest` with the following call:
 
```
restifizerManager.resourceAt(path)
```

For example,

```
restifizerManager.resourceAt("api/users")
```

`RestifizerRequest` has a set of self-explanatory methods which can be combined to a chain if calls. For example:

```
restifizerManager.resourceAt("api/users").query("name", "Jimmy").page(0, 10);
```

The example creates and configures the request to work with "api/users" path and set up `query` and `page` with `per_page`  params (see appropriate sections in Restifizer documentation https://github.com/vedi/restifizer#supported-params).

After you configured the request as a final touch you should specify http-method to call. The following methods are supported:
* GET,
* POST,
* PUT,
* PATCH,
* DELETE.

When you call GET you provide just `callback`, for other methods you additionaly provide `params` - 
Hashtable to be serialized to JSON and put to request body.

For example, getting list of the users:

```
restifizerManager.resourceAt("api/users").
        get(new RestifizerCallback() {
                    @Override
                    public void onCallback(RestifizerResponse response) {
                        //TODO on your response result
                    }
                });
```

And another example, signing up an user:

```
        JSONObject parameters = new JSONObject();
        parameters.put("username", "test");
        parameters.put("password", "test");

        restifizerManager.resourceAt("api/users").post(parameters.toString(), new RestifizerCallback() {
                    @Override
                    public void onCallback(RestifizerResponse response) {
                        //TODO on your response result
                    }
                });

```

## Accessing to exact resources

There is a lot of cases, where we need to specify ID of an affected resource in our request. 
In REST API such IDs are specified as an additional part of resource path. 
In Restifizer SDK in order to focus a request on an exact record you should call `One` method, passing ID there.
    
For example, we are changing an user's password:
```
        JSONObject parameters = new JSONObject();
      
        parameters.put("password", "test");

        restifizerManager.resourceAt("api/users").one(userId).patch(parameters.toString(), new RestifizerCallback() {
                    @Override
                    public void onCallback(RestifizerResponse response) {
                        //TODO on your response result
                    }
                });
        
```

## Authentication

Restifizer SDK support 2 kinds of authentications: 
* Client Credentials (https://tools.ietf.org/html/rfc6749#section-1.3.4)
* Access Token (https://tools.ietf.org/html/rfc6749#section-1.4)
 
In order to use it in your request you need to configure `RestifizerManager` before.

### Client Credentials

Configuration:

```
restifizerManager.configClientAuth(CLIENT_ID, CLIENT_SECRET);
```

Using (add `withClientAuth()` to the chain):

```
restifizerManager.resourceAt("api/users").
        withClientAuth().
        get(new RestifizerCallback() {
                    @Override
                    public void onCallback(RestifizerResponse response) {
                        //TODO on your response result
                    }
                });
```

### Access Token

Configuration:

```
restifizerManager.configBearerAuth(accessToken);
```

Using (add `withBearerAuth()` to the chain):

```
        restifizerManager.resourceAt("api/users").
                withBearerAuth().
                one(userId).
                patch(parameters.toString(), new RestifizerCallback() {
                    @Override
                    public void onCallback(RestifizerResponse response) {
                        //TODO on your response result
                    }
                });
```

## Response

Response from the server is wrapped with `RestifizerResponse` that provides you with parsed data. 
Response contain data accquired from the server. 
To access it you need use restifizerResponse.response
    
## Error Handling

There are 2 approaches to handle error in the SDK: via EventHandler, and in callbacks of requests.

If you set `ErrorHandler` of `RestifizerManager` with instance of `IErrorHandler`, every error will pass through its method
`bool onRestifizerError(RestifizerError restifizerError)`. 
If you want to suppress further handling in `callback` of the request, you should return `false` in this method.
Otherwise, your callback will be called, with `Error` propery of response set to instance of `RestifizerError`.
  
For example,

```
    public bool onRestifizerError(RestifizerError restifizerError) {
        Log.w(TAG, restifizerError.Message);
        return true;    // do not stop propagating
    }

    public void exitGame() {

        JSONObject parameters = new JSONObject();
        . . .
        restifizerManager.кesourceAt("api/games/").one(gameId).withBearerAuth().patch(parameters, new RestifizerCallback() {
                    @Override
                    public void onCallback(RestifizerResponse response) {
                        //TODO on your response result
                    }
                });
    }
```


