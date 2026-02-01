<!-- Generator: Widdershins v4.0.1 -->

<h1 id="blog-api">Blog API v1</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

Auto-generated OpenAPI spec via springdoc-openapi.

- Web APIs: /api
- Admin APIs: /api/admin

Admin APIs require: Authorization: Bearer <token>

Base URLs:

* <a href="http://localhost:8080">http://localhost:8080</a>

# Authentication

- HTTP Authentication, scheme: bearer 

<h1 id="blog-api-admin-auth">Admin - Auth</h1>

Admin authentication

## login

<a id="opIdlogin"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/admin/auth/login \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "username": "admin",
  "password": "123456"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/auth/login',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/admin/auth/login`

*Admin login*

Login by username/password and get JWT token.

> Body parameter

```json
{
  "username": "admin",
  "password": "123456"
}
```

<h3 id="login-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[AdminLoginRequest](#schemaadminloginrequest)|true|none|

> Example responses

> 200 Response

<h3 id="login-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAdminLoginResponse](#schemaapiresponseadminloginresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## me

<a id="opIdme"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/auth/me \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/auth/me',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/auth/me`

*Get current admin*

Get current admin profile by JWT principal.

> Example responses

> 200 Response

<h3 id="me-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAdminMeResponse](#schemaapiresponseadminmeresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-comments">Admin - Comments</h1>

Comment moderation

## reject

<a id="opIdreject"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/comments/{id}/reject \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/comments/{id}/reject',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/comments/{id}/reject`

*Reject comment*

<h3 id="reject-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="reject-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## approve

<a id="opIdapprove"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/comments/{id}/approve \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/comments/{id}/approve',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/comments/{id}/approve`

*Approve comment*

<h3 id="approve-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="approve-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## page_2

<a id="opIdpage_2"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/comments?q=pageNum,1,pageSize,10,status,PENDING,postId,1 \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/comments?q=pageNum,1,pageSize,10,status,PENDING,postId,1',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/comments`

*List comments*

Admin comment list with pagination and optional filters (status/postId).

<h3 id="page_2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|q|query|[AdminCommentQuery](#schemaadmincommentquery)|true|none|

> Example responses

> 200 Response

<h3 id="page_2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageResultAdminCommentVO](#schemaapiresponsepageresultadmincommentvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## delete_3

<a id="opIddelete_3"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/admin/comments/{id} \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/comments/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/admin/comments/{id}`

*Delete comment*

<h3 id="delete_3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="delete_3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-resources">Admin - Resources</h1>

Uploaded file resources

## page

<a id="opIdpage"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/resources?q=pageNum,1,pageSize,10,keyword,banner,contentTypePrefix,image%2F \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/resources?q=pageNum,1,pageSize,10,keyword,banner,contentTypePrefix,image%2F',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/resources`

*List resources*

List uploaded resources with pagination and optional filters.

<h3 id="page-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|q|query|[AdminResourceQuery](#schemaadminresourcequery)|true|none|

> Example responses

> 200 Response

<h3 id="page-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageResultFileResourceVO](#schemaapiresponsepageresultfileresourcevo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## delete_1

<a id="opIddelete_1"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/admin/resources/{id} \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/resources/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/admin/resources/{id}`

*Delete resource*

Deletes resource record and removes file from disk if possible.

<h3 id="delete_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="delete_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMapStringObject](#schemaapiresponsemapstringobject)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-web-health">Web - Health</h1>

Health check

## health

<a id="opIdhealth"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/health \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/health',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/health`

*Health check*

> Example responses

> 200 Response

<h3 id="health-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMapStringObject](#schemaapiresponsemapstringobject)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-admin-users">Admin - Admin Users</h1>

Admin user management

## status

<a id="opIdstatus"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/admins/{id}/status \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "status": 1
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/admins/{id}/status',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/admins/{id}/status`

*Update admin status*

Enable or disable an admin user.

> Body parameter

```json
{
  "status": 1
}
```

<h3 id="status-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|
|body|body|[AdminUserStatusRequest](#schemaadminuserstatusrequest)|true|none|

> Example responses

> 200 Response

<h3 id="status-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMapStringObject](#schemaapiresponsemapstringobject)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## resetPassword

<a id="opIdresetPassword"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/admins/{id}/reset-password \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "newPassword": "123456"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/admins/{id}/reset-password',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/admins/{id}/reset-password`

*Reset admin password*

> Body parameter

```json
{
  "newPassword": "123456"
}
```

<h3 id="resetpassword-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|
|body|body|[AdminUserResetPasswordRequest](#schemaadminuserresetpasswordrequest)|true|none|

> Example responses

> 200 Response

<h3 id="resetpassword-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMapStringObject](#schemaapiresponsemapstringobject)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## list_6

<a id="opIdlist_6"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/admins \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/admins',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/admins`

*List admins*

> Example responses

> 200 Response

<h3 id="list_6-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListAdminUserVO](#schemaapiresponselistadminuservo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## create_4

<a id="opIdcreate_4"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/admin/admins \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "username": "admin2",
  "password": "123456",
  "displayName": "Editor"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/admins',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/admin/admins`

*Create admin*

> Body parameter

```json
{
  "username": "admin2",
  "password": "123456",
  "displayName": "Editor"
}
```

<h3 id="create_4-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[AdminUserCreateRequest](#schemaadminusercreaterequest)|true|none|

> Example responses

> 200 Response

<h3 id="create_4-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseMapStringObject](#schemaapiresponsemapstringobject)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-web-categories">Web - Categories</h1>

Public categories

## list_3

<a id="opIdlist_3"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/categories \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/categories',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/categories`

*List categories*

Returns all categories.

> Example responses

> 200 Response

<h3 id="list_3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListCategoryVO](#schemaapiresponselistcategoryvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-web-posts">Web - Posts</h1>

Public post browsing APIs

## list_1

<a id="opIdlist_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/posts?q=pageNum,1,pageSize,10,keyword,spring,categoryId,1,tagId,2 \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/posts?q=pageNum,1,pageSize,10,keyword,spring,categoryId,1,tagId,2',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/posts`

*List published posts*

Supports pagination and optional filters (keyword/categoryId/tagId).

<h3 id="list_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|q|query|[PostQuery](#schemapostquery)|true|none|

> Example responses

> 200 Response

<h3 id="list_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageResultPostListItemVO](#schemaapiresponsepageresultpostlistitemvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## detail

<a id="opIddetail"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/posts/{id} \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/posts/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/posts/{id}`

*Get published post detail*

Returns post detail and increases viewCount by 1.

<h3 id="detail-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="detail-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePostDetailVO](#schemaapiresponsepostdetailvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## hot

<a id="opIdhot"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/posts/hot \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/posts/hot',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/posts/hot`

*Get hot posts*

Returns latest hot posts ordered by view count.

<h3 id="hot-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|limit|query|integer(int32)|false|none|

> Example responses

> 200 Response

<h3 id="hot-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListHotPostVO](#schemaapiresponselisthotpostvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## archives

<a id="opIdarchives"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/posts/archives \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/posts/archives',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/posts/archives`

*Get archives*

Returns archive groups aggregated by month.

> Example responses

> 200 Response

<h3 id="archives-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListArchiveMonthGroupVO](#schemaapiresponselistarchivemonthgroupvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-web-tags">Web - Tags</h1>

Public tags

## list

<a id="opIdlist"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/tags \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/tags',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/tags`

*List tags*

Returns all tags.

> Example responses

> 200 Response

<h3 id="list-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListTagVO](#schemaapiresponselisttagvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-site">Admin - Site</h1>

Site settings management

## get_1

<a id="opIdget_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/site \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/site',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/site`

*Get site settings*

> Example responses

> 200 Response

<h3 id="get_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseSiteSettingVO](#schemaapiresponsesitesettingvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## update_1

<a id="opIdupdate_1"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/site \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "siteName": "My Blog",
  "siteNotice": "Welcome to my blog",
  "aboutContent": "# About\n\nThis is my blog...",
  "linksJson": "[{\"name\":\"GitHub\",\"url\":\"https://github.com/xxx\"}]",
  "seoTitle": "My Blog",
  "seoKeywords": "java,vue,spring",
  "seoDescription": "A personal tech blog",
  "footerText": "Copyright © 2026",
  "bannerUrl": "/uploads/202602/banner.jpg"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/site',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/site`

*Update site settings*

> Body parameter

```json
{
  "siteName": "My Blog",
  "siteNotice": "Welcome to my blog",
  "aboutContent": "# About\n\nThis is my blog...",
  "linksJson": "[{\"name\":\"GitHub\",\"url\":\"https://github.com/xxx\"}]",
  "seoTitle": "My Blog",
  "seoKeywords": "java,vue,spring",
  "seoDescription": "A personal tech blog",
  "footerText": "Copyright © 2026",
  "bannerUrl": "/uploads/202602/banner.jpg"
}
```

<h3 id="update_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[SiteSettingRequest](#schemasitesettingrequest)|true|none|

> Example responses

> 200 Response

<h3 id="update_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-posts">Admin - Posts</h1>

Post management

## detail_1

<a id="opIddetail_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/posts/{id} \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts/{id}',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/posts/{id}`

*Get post detail for editing*

<h3 id="detail_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="detail_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAdminPostEditVO](#schemaapiresponseadminposteditvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## update_2

<a id="opIdupdate_2"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/posts/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "title": "Hello World",
  "summary": "A short introduction about this post",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "categoryId": 1,
  "tagIds": [
    1,
    2,
    3
  ]
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/posts/{id}`

*Update post*

> Body parameter

```json
{
  "title": "Hello World",
  "summary": "A short introduction about this post",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "categoryId": 1,
  "tagIds": [
    1,
    2,
    3
  ]
}
```

<h3 id="update_2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|
|body|body|[AdminPostUpdateRequest](#schemaadminpostupdaterequest)|true|none|

> Example responses

> 200 Response

<h3 id="update_2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## delete_2

<a id="opIddelete_2"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/admin/posts/{id} \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/admin/posts/{id}`

*Delete post*

<h3 id="delete_2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="delete_2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## unpublish

<a id="opIdunpublish"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/posts/{id}/unpublish \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts/{id}/unpublish',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/posts/{id}/unpublish`

*Unpublish post*

<h3 id="unpublish-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="unpublish-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## publish

<a id="opIdpublish"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/posts/{id}/publish \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts/{id}/publish',
{
  method: 'PUT',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/posts/{id}/publish`

*Publish post*

<h3 id="publish-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="publish-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## page_1

<a id="opIdpage_1"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/posts?q=pageNum,1,pageSize,10,status,PUBLISHED,keyword,spring,categoryId,1,tagId,2 \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts?q=pageNum,1,pageSize,10,status,PUBLISHED,keyword,spring,categoryId,1,tagId,2',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/posts`

*List posts*

Admin post list with pagination and filters.

<h3 id="page_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|q|query|[AdminPostQuery](#schemaadminpostquery)|true|none|

> Example responses

> 200 Response

<h3 id="page_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponsePageResultAdminPostListItemVO](#schemaapiresponsepageresultadminpostlistitemvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## create_2

<a id="opIdcreate_2"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/admin/posts \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "title": "Hello World",
  "summary": "A short introduction about this post",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "categoryId": 1,
  "tagIds": [
    1,
    2,
    3
  ]
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/admin/posts`

*Create post*

> Body parameter

```json
{
  "title": "Hello World",
  "summary": "A short introduction about this post",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "categoryId": 1,
  "tagIds": [
    1,
    2,
    3
  ]
}
```

<h3 id="create_2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[AdminPostCreateRequest](#schemaadminpostcreaterequest)|true|none|

> Example responses

> 200 Response

<h3 id="create_2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseLong](#schemaapiresponselong)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## tags

<a id="opIdtags"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/posts/meta/tags \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts/meta/tags',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/posts/meta/tags`

*List tags for editor*

> Example responses

> 200 Response

<h3 id="tags-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListTagVO](#schemaapiresponselisttagvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## categories

<a id="opIdcategories"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/posts/meta/categories \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/posts/meta/categories',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/posts/meta/categories`

*List categories for editor*

> Example responses

> 200 Response

<h3 id="categories-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListCategoryVO](#schemaapiresponselistcategoryvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-web-comments">Web - Comments</h1>

Public comment APIs

## list_2

<a id="opIdlist_2"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/posts/{id}/comments \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/posts/{id}/comments',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/posts/{id}/comments`

*List approved comments*

Lists comments for a post (APPROVED only).

<h3 id="list_2-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="list_2-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListCommentVO](#schemaapiresponselistcommentvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## create

<a id="opIdcreate"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/posts/{id}/comments \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "nickname": "Tom",
  "email": "tom@example.com",
  "content": "Nice post!"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/posts/{id}/comments',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/posts/{id}/comments`

*Create comment*

Creates a comment for a post. New comments default to PENDING for admin review.

> Body parameter

```json
{
  "nickname": "Tom",
  "email": "tom@example.com",
  "content": "Nice post!"
}
```

<h3 id="create-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|
|body|body|[CommentCreateRequest](#schemacommentcreaterequest)|true|none|

> Example responses

> 200 Response

<h3 id="create-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseLong](#schemaapiresponselong)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-categories">Admin - Categories</h1>

Category management

## update_3

<a id="opIdupdate_3"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/categories/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "name": "Java"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/categories/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/categories/{id}`

*Update category*

> Body parameter

```json
{
  "name": "Java"
}
```

<h3 id="update_3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|
|body|body|[NameOnlyRequest](#schemanameonlyrequest)|true|none|

> Example responses

> 200 Response

<h3 id="update_3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## delete_4

<a id="opIddelete_4"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/admin/categories/{id} \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/categories/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/admin/categories/{id}`

*Delete category*

<h3 id="delete_4-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="delete_4-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## list_5

<a id="opIdlist_5"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/categories \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/categories',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/categories`

*List categories*

> Example responses

> 200 Response

<h3 id="list_5-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListCategoryVO](#schemaapiresponselistcategoryvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## create_3

<a id="opIdcreate_3"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/admin/categories \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "name": "Java"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/categories',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/admin/categories`

*Create category*

> Body parameter

```json
{
  "name": "Java"
}
```

<h3 id="create_3-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[NameOnlyRequest](#schemanameonlyrequest)|true|none|

> Example responses

> 200 Response

<h3 id="create_3-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseLong](#schemaapiresponselong)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-tags">Admin - Tags</h1>

Tag management

## update

<a id="opIdupdate"></a>

> Code samples

```shell
# You can also use wget
curl -X PUT http://localhost:8080/api/admin/tags/{id} \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "name": "Java"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/tags/{id}',
{
  method: 'PUT',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`PUT /api/admin/tags/{id}`

*Update tag*

> Body parameter

```json
{
  "name": "Java"
}
```

<h3 id="update-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|
|body|body|[NameOnlyRequest](#schemanameonlyrequest)|true|none|

> Example responses

> 200 Response

<h3 id="update-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## delete

<a id="opIddelete"></a>

> Code samples

```shell
# You can also use wget
curl -X DELETE http://localhost:8080/api/admin/tags/{id} \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/tags/{id}',
{
  method: 'DELETE',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`DELETE /api/admin/tags/{id}`

*Delete tag*

<h3 id="delete-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|id|path|integer(int64)|true|none|

> Example responses

> 200 Response

<h3 id="delete-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseVoid](#schemaapiresponsevoid)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## list_4

<a id="opIdlist_4"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/tags \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/tags',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/tags`

*List tags*

> Example responses

> 200 Response

<h3 id="list_4-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseListTagVO](#schemaapiresponselisttagvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

## create_1

<a id="opIdcreate_1"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/admin/tags \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "name": "Java"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/tags',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/admin/tags`

*Create tag*

> Body parameter

```json
{
  "name": "Java"
}
```

<h3 id="create_1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[NameOnlyRequest](#schemanameonlyrequest)|true|none|

> Example responses

> 200 Response

<h3 id="create_1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseLong](#schemaapiresponselong)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-web-site">Web - Site</h1>

Public site settings

## get

<a id="opIdget"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/site \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/site',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/site`

*Get site settings*

Fetch site settings for frontend rendering (siteName/notice/about/SEO/footer/banner).

> Example responses

> 200 Response

<h3 id="get-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseSiteSettingVO](#schemaapiresponsesitesettingvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-upload">Admin - Upload</h1>

Upload APIs

## uploadImage

<a id="opIduploadImage"></a>

> Code samples

```shell
# You can also use wget
curl -X POST http://localhost:8080/api/admin/upload/image \
  -H 'Content-Type: application/json' \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript
const inputBody = '{
  "file": "string"
}';
const headers = {
  'Content-Type':'application/json',
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/upload/image',
{
  method: 'POST',
  body: inputBody,
  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`POST /api/admin/upload/image`

*Upload image*

Upload an image file and get a public /uploads/** URL.

> Body parameter

```json
{
  "file": "string"
}
```

<h3 id="uploadimage-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|object|true|none|
|» file|body|string(binary)|true|none|

> Example responses

> 200 Response

<h3 id="uploadimage-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseUploadResultVO](#schemaapiresponseuploadresultvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

<h1 id="blog-api-admin-dashboard-controller">admin-dashboard-controller</h1>

## stats

<a id="opIdstats"></a>

> Code samples

```shell
# You can also use wget
curl -X GET http://localhost:8080/api/admin/dashboard/stats \
  -H 'Accept: */*' \
  -H 'Authorization: Bearer {access-token}'

```

```javascript

const headers = {
  'Accept':'*/*',
  'Authorization':'Bearer {access-token}'
};

fetch('http://localhost:8080/api/admin/dashboard/stats',
{
  method: 'GET',

  headers: headers
})
.then(function(res) {
    return res.json();
}).then(function(body) {
    console.log(body);
});

```

`GET /api/admin/dashboard/stats`

> Example responses

> 200 Response

<h3 id="stats-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[ApiResponseAdminDashboardStatsVO](#schemaapiresponseadmindashboardstatsvo)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad Request|[ApiResponseVoid](#schemaapiresponsevoid)|
|401|[Unauthorized](https://tools.ietf.org/html/rfc7235#section-3.1)|Unauthorized|[ApiResponseVoid](#schemaapiresponsevoid)|
|403|[Forbidden](https://tools.ietf.org/html/rfc7231#section-6.5.3)|Forbidden|[ApiResponseVoid](#schemaapiresponsevoid)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Internal Server Error|[ApiResponseVoid](#schemaapiresponsevoid)|

<aside class="warning">
To perform this operation, you must be authenticated by means of one of the following methods:
bearer-jwt
</aside>

# Schemas

<h2 id="tocS_ApiResponseVoid">ApiResponseVoid</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsevoid"></a>
<a id="schema_ApiResponseVoid"></a>
<a id="tocSapiresponsevoid"></a>
<a id="tocsapiresponsevoid"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {},
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|object|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_NameOnlyRequest">NameOnlyRequest</h2>
<!-- backwards compatibility -->
<a id="schemanameonlyrequest"></a>
<a id="schema_NameOnlyRequest"></a>
<a id="tocSnameonlyrequest"></a>
<a id="tocsnameonlyrequest"></a>

```json
{
  "name": "Java"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|true|none|Name|

<h2 id="tocS_SiteSettingRequest">SiteSettingRequest</h2>
<!-- backwards compatibility -->
<a id="schemasitesettingrequest"></a>
<a id="schema_SiteSettingRequest"></a>
<a id="tocSsitesettingrequest"></a>
<a id="tocssitesettingrequest"></a>

```json
{
  "siteName": "My Blog",
  "siteNotice": "Welcome to my blog",
  "aboutContent": "# About\n\nThis is my blog...",
  "linksJson": "[{\"name\":\"GitHub\",\"url\":\"https://github.com/xxx\"}]",
  "seoTitle": "My Blog",
  "seoKeywords": "java,vue,spring",
  "seoDescription": "A personal tech blog",
  "footerText": "Copyright © 2026",
  "bannerUrl": "/uploads/202602/banner.jpg"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|siteName|string|true|none|Site name|
|siteNotice|string|false|none|Site notice (displayed on home/detail pages)|
|aboutContent|string|false|none|About page content in Markdown|
|linksJson|string|false|none|Friend links as JSON string, e.g. [{"name":"GitHub","url":"https://..."}]|
|seoTitle|string|false|none|SEO title|
|seoKeywords|string|false|none|SEO keywords|
|seoDescription|string|false|none|SEO description|
|footerText|string|false|none|Footer text|
|bannerUrl|string|false|none|Homepage full-screen banner image URL (under /uploads)|

<h2 id="tocS_AdminPostUpdateRequest">AdminPostUpdateRequest</h2>
<!-- backwards compatibility -->
<a id="schemaadminpostupdaterequest"></a>
<a id="schema_AdminPostUpdateRequest"></a>
<a id="tocSadminpostupdaterequest"></a>
<a id="tocsadminpostupdaterequest"></a>

```json
{
  "title": "Hello World",
  "summary": "A short introduction about this post",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "categoryId": 1,
  "tagIds": [
    1,
    2,
    3
  ]
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|title|string|true|none|Post title|
|summary|string|false|none|Post summary|
|content|string|true|none|Post content in Markdown|
|coverUrl|string|false|none|Cover image URL (usually under /uploads)|
|categoryId|integer(int64)|false|none|Category id|
|tagIds|[integer]|false|none|Tag ids|

<h2 id="tocS_AdminUserStatusRequest">AdminUserStatusRequest</h2>
<!-- backwards compatibility -->
<a id="schemaadminuserstatusrequest"></a>
<a id="schema_AdminUserStatusRequest"></a>
<a id="tocSadminuserstatusrequest"></a>
<a id="tocsadminuserstatusrequest"></a>

```json
{
  "status": 1
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|status|integer(int32)|true|none|Status: 1=enabled, 0=disabled|

<h2 id="tocS_ApiResponseMapStringObject">ApiResponseMapStringObject</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsemapstringobject"></a>
<a id="schema_ApiResponseMapStringObject"></a>
<a id="tocSapiresponsemapstringobject"></a>
<a id="tocsapiresponsemapstringobject"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "property1": {},
    "property2": {}
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|object|false|none|Response data payload|
|» **additionalProperties**|object|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_AdminUserResetPasswordRequest">AdminUserResetPasswordRequest</h2>
<!-- backwards compatibility -->
<a id="schemaadminuserresetpasswordrequest"></a>
<a id="schema_AdminUserResetPasswordRequest"></a>
<a id="tocSadminuserresetpasswordrequest"></a>
<a id="tocsadminuserresetpasswordrequest"></a>

```json
{
  "newPassword": "123456"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|newPassword|string|true|none|New password|

<h2 id="tocS_CommentCreateRequest">CommentCreateRequest</h2>
<!-- backwards compatibility -->
<a id="schemacommentcreaterequest"></a>
<a id="schema_CommentCreateRequest"></a>
<a id="tocScommentcreaterequest"></a>
<a id="tocscommentcreaterequest"></a>

```json
{
  "nickname": "Tom",
  "email": "tom@example.com",
  "content": "Nice post!"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|nickname|string|true|none|Nickname displayed on comment|
|email|string|false|none|Email (optional). Used for contact only, not displayed publicly|
|content|string|true|none|Comment content|

<h2 id="tocS_ApiResponseLong">ApiResponseLong</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselong"></a>
<a id="schema_ApiResponseLong"></a>
<a id="tocSapiresponselong"></a>
<a id="tocsapiresponselong"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": 0,
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|integer(int64)|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_ApiResponseUploadResultVO">ApiResponseUploadResultVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseuploadresultvo"></a>
<a id="schema_ApiResponseUploadResultVO"></a>
<a id="tocSapiresponseuploadresultvo"></a>
<a id="tocsapiresponseuploadresultvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "url": "/uploads/202602/xxx.jpg",
    "originalName": "banner.jpg",
    "size": 12345,
    "contentType": "image/jpeg"
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[UploadResultVO](#schemauploadresultvo)|false|none|Upload result|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_UploadResultVO">UploadResultVO</h2>
<!-- backwards compatibility -->
<a id="schemauploadresultvo"></a>
<a id="schema_UploadResultVO"></a>
<a id="tocSuploadresultvo"></a>
<a id="tocsuploadresultvo"></a>

```json
{
  "url": "/uploads/202602/xxx.jpg",
  "originalName": "banner.jpg",
  "size": 12345,
  "contentType": "image/jpeg"
}

```

Upload result

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|url|string|false|none|Public URL|
|originalName|string|false|none|Original file name|
|size|integer(int64)|false|none|File size in bytes|
|contentType|string|false|none|Content type|

<h2 id="tocS_AdminPostCreateRequest">AdminPostCreateRequest</h2>
<!-- backwards compatibility -->
<a id="schemaadminpostcreaterequest"></a>
<a id="schema_AdminPostCreateRequest"></a>
<a id="tocSadminpostcreaterequest"></a>
<a id="tocsadminpostcreaterequest"></a>

```json
{
  "title": "Hello World",
  "summary": "A short introduction about this post",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "categoryId": 1,
  "tagIds": [
    1,
    2,
    3
  ]
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|title|string|true|none|Post title|
|summary|string|false|none|Post summary|
|content|string|true|none|Post content in Markdown|
|coverUrl|string|false|none|Cover image URL (usually under /uploads)|
|categoryId|integer(int64)|false|none|Category id|
|tagIds|[integer]|false|none|Tag ids|

<h2 id="tocS_AdminLoginRequest">AdminLoginRequest</h2>
<!-- backwards compatibility -->
<a id="schemaadminloginrequest"></a>
<a id="schema_AdminLoginRequest"></a>
<a id="tocSadminloginrequest"></a>
<a id="tocsadminloginrequest"></a>

```json
{
  "username": "admin",
  "password": "123456"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|username|string|true|none|Admin username|
|password|string|true|none|Admin password (plain text, will be verified server-side)|

<h2 id="tocS_AdminLoginResponse">AdminLoginResponse</h2>
<!-- backwards compatibility -->
<a id="schemaadminloginresponse"></a>
<a id="schema_AdminLoginResponse"></a>
<a id="tocSadminloginresponse"></a>
<a id="tocsadminloginresponse"></a>

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}

```

Admin login response

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|token|string|false|none|JWT token|

<h2 id="tocS_ApiResponseAdminLoginResponse">ApiResponseAdminLoginResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseadminloginresponse"></a>
<a id="schema_ApiResponseAdminLoginResponse"></a>
<a id="tocSapiresponseadminloginresponse"></a>
<a id="tocsapiresponseadminloginresponse"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[AdminLoginResponse](#schemaadminloginresponse)|false|none|Admin login response|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_AdminUserCreateRequest">AdminUserCreateRequest</h2>
<!-- backwards compatibility -->
<a id="schemaadminusercreaterequest"></a>
<a id="schema_AdminUserCreateRequest"></a>
<a id="tocSadminusercreaterequest"></a>
<a id="tocsadminusercreaterequest"></a>

```json
{
  "username": "admin2",
  "password": "123456",
  "displayName": "Editor"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|username|string|true|none|Login username|
|password|string|true|none|Initial password|
|displayName|string|false|none|Display name (optional)|

<h2 id="tocS_ApiResponseListTagVO">ApiResponseListTagVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselisttagvo"></a>
<a id="schema_ApiResponseListTagVO"></a>
<a id="tocSapiresponselisttagvo"></a>
<a id="tocsapiresponselisttagvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Spring"
    }
  ],
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[[TagVO](#schematagvo)]|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_TagVO">TagVO</h2>
<!-- backwards compatibility -->
<a id="schematagvo"></a>
<a id="schema_TagVO"></a>
<a id="tocStagvo"></a>
<a id="tocstagvo"></a>

```json
{
  "id": 1,
  "name": "Spring"
}

```

Tag

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Tag id|
|name|string|false|none|Tag name|

<h2 id="tocS_ApiResponseSiteSettingVO">ApiResponseSiteSettingVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsesitesettingvo"></a>
<a id="schema_ApiResponseSiteSettingVO"></a>
<a id="tocSapiresponsesitesettingvo"></a>
<a id="tocsapiresponsesitesettingvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "siteName": "我的博客",
    "siteNotice": "欢迎光临",
    "aboutContent": "# 关于\n\n...",
    "linksJson": "[{\"name\":\"GitHub\",\"url\":\"https://github.com/xxx\"}]",
    "seoTitle": "我的博客",
    "seoKeywords": "java,vue,spring",
    "seoDescription": "一个个人技术博客",
    "footerText": "版权所有 © 2026",
    "bannerUrl": "/uploads/202602/banner.jpg"
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[SiteSettingVO](#schemasitesettingvo)|false|none|站点设置|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_SiteSettingVO">SiteSettingVO</h2>
<!-- backwards compatibility -->
<a id="schemasitesettingvo"></a>
<a id="schema_SiteSettingVO"></a>
<a id="tocSsitesettingvo"></a>
<a id="tocssitesettingvo"></a>

```json
{
  "siteName": "我的博客",
  "siteNotice": "欢迎光临",
  "aboutContent": "# 关于\n\n...",
  "linksJson": "[{\"name\":\"GitHub\",\"url\":\"https://github.com/xxx\"}]",
  "seoTitle": "我的博客",
  "seoKeywords": "java,vue,spring",
  "seoDescription": "一个个人技术博客",
  "footerText": "版权所有 © 2026",
  "bannerUrl": "/uploads/202602/banner.jpg"
}

```

站点设置

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|siteName|string|false|none|站点名称|
|siteNotice|string|false|none|站点公告|
|aboutContent|string|false|none|关于内容（Markdown 格式）|
|linksJson|string|false|none|链接 JSON 字符串|
|seoTitle|string|false|none|SEO 标题|
|seoKeywords|string|false|none|SEO 关键字|
|seoDescription|string|false|none|SEO 描述|
|footerText|string|false|none|页脚文本|
|bannerUrl|string|false|none|首页全屏横幅图片 URL|

<h2 id="tocS_PostQuery">PostQuery</h2>
<!-- backwards compatibility -->
<a id="schemapostquery"></a>
<a id="schema_PostQuery"></a>
<a id="tocSpostquery"></a>
<a id="tocspostquery"></a>

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "spring",
  "categoryId": 1,
  "tagId": 2
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|pageNum|integer(int64)|false|none|Page number (start from 1)|
|pageSize|integer(int64)|false|none|Page size (1~100)|
|keyword|string|false|none|Optional keyword for title/summary search|
|categoryId|integer(int64)|false|none|Filter by categoryId|
|tagId|integer(int64)|false|none|Filter by tagId|

<h2 id="tocS_ApiResponsePageResultPostListItemVO">ApiResponsePageResultPostListItemVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresultpostlistitemvo"></a>
<a id="schema_ApiResponsePageResultPostListItemVO"></a>
<a id="tocSapiresponsepageresultpostlistitemvo"></a>
<a id="tocsapiresponsepageresultpostlistitemvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "Hello World",
        "summary": "Short introduction",
        "coverUrl": "/uploads/202602/cover.jpg",
        "category": {
          "id": 1,
          "name": "Java"
        },
        "tags": [
          {
            "id": 1,
            "name": "Spring"
          }
        ],
        "publishedAt": "2026-02-01 12:00:00",
        "viewCount": 123
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[PageResultPostListItemVO](#schemapageresultpostlistitemvo)|false|none|Pagination result wrapper|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_CategoryVO">CategoryVO</h2>
<!-- backwards compatibility -->
<a id="schemacategoryvo"></a>
<a id="schema_CategoryVO"></a>
<a id="tocScategoryvo"></a>
<a id="tocscategoryvo"></a>

```json
{
  "id": 1,
  "name": "Java"
}

```

Category

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Category id|
|name|string|false|none|Category name|

<h2 id="tocS_PageResultPostListItemVO">PageResultPostListItemVO</h2>
<!-- backwards compatibility -->
<a id="schemapageresultpostlistitemvo"></a>
<a id="schema_PageResultPostListItemVO"></a>
<a id="tocSpageresultpostlistitemvo"></a>
<a id="tocspageresultpostlistitemvo"></a>

```json
{
  "list": [
    {
      "id": 1,
      "title": "Hello World",
      "summary": "Short introduction",
      "coverUrl": "/uploads/202602/cover.jpg",
      "category": {
        "id": 1,
        "name": "Java"
      },
      "tags": [
        {
          "id": 1,
          "name": "Spring"
        }
      ],
      "publishedAt": "2026-02-01 12:00:00",
      "viewCount": 123
    }
  ],
  "total": 100,
  "pageNum": 1,
  "pageSize": 10
}

```

Pagination result wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|list|[[PostListItemVO](#schemapostlistitemvo)]|false|none|Current page list|
|total|integer(int64)|false|none|Total items count|
|pageNum|integer(int64)|false|none|Current page number|
|pageSize|integer(int64)|false|none|Page size|

<h2 id="tocS_PostListItemVO">PostListItemVO</h2>
<!-- backwards compatibility -->
<a id="schemapostlistitemvo"></a>
<a id="schema_PostListItemVO"></a>
<a id="tocSpostlistitemvo"></a>
<a id="tocspostlistitemvo"></a>

```json
{
  "id": 1,
  "title": "Hello World",
  "summary": "Short introduction",
  "coverUrl": "/uploads/202602/cover.jpg",
  "category": {
    "id": 1,
    "name": "Java"
  },
  "tags": [
    {
      "id": 1,
      "name": "Spring"
    }
  ],
  "publishedAt": "2026-02-01 12:00:00",
  "viewCount": 123
}

```

Post list item

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Post id|
|title|string|false|none|Post title|
|summary|string|false|none|Post summary|
|coverUrl|string|false|none|Cover image URL|
|category|[CategoryVO](#schemacategoryvo)|false|none|Category|
|tags|[[TagVO](#schematagvo)]|false|none|Tags|
|publishedAt|string|false|none|Published time (string)|
|viewCount|integer(int64)|false|none|View count|

<h2 id="tocS_ApiResponsePostDetailVO">ApiResponsePostDetailVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepostdetailvo"></a>
<a id="schema_ApiResponsePostDetailVO"></a>
<a id="tocSapiresponsepostdetailvo"></a>
<a id="tocsapiresponsepostdetailvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1,
    "title": "Hello World",
    "summary": "Short introduction",
    "content": "# Heading\n\nContent...",
    "coverUrl": "/uploads/202602/cover.jpg",
    "category": {
      "id": 1,
      "name": "Java"
    },
    "tags": [
      {
        "id": 1,
        "name": "Spring"
      }
    ],
    "publishedAt": "2026-02-01 12:00:00",
    "viewCount": 123
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[PostDetailVO](#schemapostdetailvo)|false|none|Post detail|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_PostDetailVO">PostDetailVO</h2>
<!-- backwards compatibility -->
<a id="schemapostdetailvo"></a>
<a id="schema_PostDetailVO"></a>
<a id="tocSpostdetailvo"></a>
<a id="tocspostdetailvo"></a>

```json
{
  "id": 1,
  "title": "Hello World",
  "summary": "Short introduction",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "category": {
    "id": 1,
    "name": "Java"
  },
  "tags": [
    {
      "id": 1,
      "name": "Spring"
    }
  ],
  "publishedAt": "2026-02-01 12:00:00",
  "viewCount": 123
}

```

Post detail

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Post id|
|title|string|false|none|Post title|
|summary|string|false|none|Post summary|
|content|string|false|none|Post content in Markdown|
|coverUrl|string|false|none|Cover image URL|
|category|[CategoryVO](#schemacategoryvo)|false|none|Category|
|tags|[[TagVO](#schematagvo)]|false|none|Tags|
|publishedAt|string|false|none|Published time (string)|
|viewCount|integer(int64)|false|none|View count|

<h2 id="tocS_ApiResponseListCommentVO">ApiResponseListCommentVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistcommentvo"></a>
<a id="schema_ApiResponseListCommentVO"></a>
<a id="tocSapiresponselistcommentvo"></a>
<a id="tocsapiresponselistcommentvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "nickname": "Tom",
      "content": "Nice post!",
      "createdAt": "2026-02-01 12:00:00"
    }
  ],
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[[CommentVO](#schemacommentvo)]|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_CommentVO">CommentVO</h2>
<!-- backwards compatibility -->
<a id="schemacommentvo"></a>
<a id="schema_CommentVO"></a>
<a id="tocScommentvo"></a>
<a id="tocscommentvo"></a>

```json
{
  "id": 1,
  "nickname": "Tom",
  "content": "Nice post!",
  "createdAt": "2026-02-01 12:00:00"
}

```

Comment

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Comment id|
|nickname|string|false|none|Nickname|
|content|string|false|none|Content|
|createdAt|string|false|none|Created time|

<h2 id="tocS_ApiResponseListHotPostVO">ApiResponseListHotPostVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselisthotpostvo"></a>
<a id="schema_ApiResponseListHotPostVO"></a>
<a id="tocSapiresponselisthotpostvo"></a>
<a id="tocsapiresponselisthotpostvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "title": "Hello World",
      "viewCount": 123,
      "publishedAt": "2026-02-01 12:00:00"
    }
  ],
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[[HotPostVO](#schemahotpostvo)]|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_HotPostVO">HotPostVO</h2>
<!-- backwards compatibility -->
<a id="schemahotpostvo"></a>
<a id="schema_HotPostVO"></a>
<a id="tocShotpostvo"></a>
<a id="tocshotpostvo"></a>

```json
{
  "id": 1,
  "title": "Hello World",
  "viewCount": 123,
  "publishedAt": "2026-02-01 12:00:00"
}

```

Hot post

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Post id|
|title|string|false|none|Post title|
|viewCount|integer(int64)|false|none|View count|
|publishedAt|string|false|none|Published time|

<h2 id="tocS_ApiResponseListArchiveMonthGroupVO">ApiResponseListArchiveMonthGroupVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistarchivemonthgroupvo"></a>
<a id="schema_ApiResponseListArchiveMonthGroupVO"></a>
<a id="tocSapiresponselistarchivemonthgroupvo"></a>
<a id="tocsapiresponselistarchivemonthgroupvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "month": "2026-02",
      "count": 3,
      "posts": [
        {
          "id": 1,
          "title": "Hello World",
          "publishedAt": "2026-02-01 12:00:00"
        }
      ]
    }
  ],
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[[ArchiveMonthGroupVO](#schemaarchivemonthgroupvo)]|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_ArchiveMonthGroupVO">ArchiveMonthGroupVO</h2>
<!-- backwards compatibility -->
<a id="schemaarchivemonthgroupvo"></a>
<a id="schema_ArchiveMonthGroupVO"></a>
<a id="tocSarchivemonthgroupvo"></a>
<a id="tocsarchivemonthgroupvo"></a>

```json
{
  "month": "2026-02",
  "count": 3,
  "posts": [
    {
      "id": 1,
      "title": "Hello World",
      "publishedAt": "2026-02-01 12:00:00"
    }
  ]
}

```

Archive month group

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|month|string|false|none|Month string|
|count|integer(int32)|false|none|Posts count in this month|
|posts|[[ArchivePostVO](#schemaarchivepostvo)]|false|none|Posts|

<h2 id="tocS_ArchivePostVO">ArchivePostVO</h2>
<!-- backwards compatibility -->
<a id="schemaarchivepostvo"></a>
<a id="schema_ArchivePostVO"></a>
<a id="tocSarchivepostvo"></a>
<a id="tocsarchivepostvo"></a>

```json
{
  "id": 1,
  "title": "Hello World",
  "publishedAt": "2026-02-01 12:00:00"
}

```

Archive post item

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Post id|
|title|string|false|none|Post title|
|publishedAt|string|false|none|Published time|

<h2 id="tocS_ApiResponseListCategoryVO">ApiResponseListCategoryVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistcategoryvo"></a>
<a id="schema_ApiResponseListCategoryVO"></a>
<a id="tocSapiresponselistcategoryvo"></a>
<a id="tocsapiresponselistcategoryvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "Java"
    }
  ],
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[[CategoryVO](#schemacategoryvo)]|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_AdminResourceQuery">AdminResourceQuery</h2>
<!-- backwards compatibility -->
<a id="schemaadminresourcequery"></a>
<a id="schema_AdminResourceQuery"></a>
<a id="tocSadminresourcequery"></a>
<a id="tocsadminresourcequery"></a>

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "banner",
  "contentTypePrefix": "image/"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|pageNum|integer(int64)|false|none|Page number (start from 1)|
|pageSize|integer(int64)|false|none|Page size (1~100)|
|keyword|string|false|none|Optional keyword for fuzzy search (url/originalName)|
|contentTypePrefix|string|false|none|Optional content-type prefix filter, e.g. image/|

<h2 id="tocS_ApiResponsePageResultFileResourceVO">ApiResponsePageResultFileResourceVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresultfileresourcevo"></a>
<a id="schema_ApiResponsePageResultFileResourceVO"></a>
<a id="tocSapiresponsepageresultfileresourcevo"></a>
<a id="tocsapiresponsepageresultfileresourcevo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "url": "/uploads/202602/xxx.jpg",
        "originalName": "banner.jpg",
        "size": 12345,
        "contentType": "image/jpeg",
        "createdAt": "2026-02-01 12:00:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[PageResultFileResourceVO](#schemapageresultfileresourcevo)|false|none|Pagination result wrapper|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_FileResourceVO">FileResourceVO</h2>
<!-- backwards compatibility -->
<a id="schemafileresourcevo"></a>
<a id="schema_FileResourceVO"></a>
<a id="tocSfileresourcevo"></a>
<a id="tocsfileresourcevo"></a>

```json
{
  "id": 1,
  "url": "/uploads/202602/xxx.jpg",
  "originalName": "banner.jpg",
  "size": 12345,
  "contentType": "image/jpeg",
  "createdAt": "2026-02-01 12:00:00"
}

```

Uploaded file resource

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Resource id|
|url|string|false|none|Public URL|
|originalName|string|false|none|Original file name|
|size|integer(int64)|false|none|File size in bytes|
|contentType|string|false|none|Content type|
|createdAt|string|false|none|Created time|

<h2 id="tocS_PageResultFileResourceVO">PageResultFileResourceVO</h2>
<!-- backwards compatibility -->
<a id="schemapageresultfileresourcevo"></a>
<a id="schema_PageResultFileResourceVO"></a>
<a id="tocSpageresultfileresourcevo"></a>
<a id="tocspageresultfileresourcevo"></a>

```json
{
  "list": [
    {
      "id": 1,
      "url": "/uploads/202602/xxx.jpg",
      "originalName": "banner.jpg",
      "size": 12345,
      "contentType": "image/jpeg",
      "createdAt": "2026-02-01 12:00:00"
    }
  ],
  "total": 100,
  "pageNum": 1,
  "pageSize": 10
}

```

Pagination result wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|list|[[FileResourceVO](#schemafileresourcevo)]|false|none|Current page list|
|total|integer(int64)|false|none|Total items count|
|pageNum|integer(int64)|false|none|Current page number|
|pageSize|integer(int64)|false|none|Page size|

<h2 id="tocS_AdminPostQuery">AdminPostQuery</h2>
<!-- backwards compatibility -->
<a id="schemaadminpostquery"></a>
<a id="schema_AdminPostQuery"></a>
<a id="tocSadminpostquery"></a>
<a id="tocsadminpostquery"></a>

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "status": "PUBLISHED",
  "keyword": "spring",
  "categoryId": 1,
  "tagId": 2
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|pageNum|integer(int64)|false|none|Page number (start from 1)|
|pageSize|integer(int64)|false|none|Page size (1~100)|
|status|string|false|none|Post status (optional): DRAFT/PUBLISHED|
|keyword|string|false|none|Keyword for searching title|
|categoryId|integer(int64)|false|none|Filter by categoryId|
|tagId|integer(int64)|false|none|Filter by tagId|

<h2 id="tocS_AdminPostListItemVO">AdminPostListItemVO</h2>
<!-- backwards compatibility -->
<a id="schemaadminpostlistitemvo"></a>
<a id="schema_AdminPostListItemVO"></a>
<a id="tocSadminpostlistitemvo"></a>
<a id="tocsadminpostlistitemvo"></a>

```json
{
  "id": 1,
  "title": "Hello World",
  "status": "PUBLISHED",
  "category": {
    "id": 1,
    "name": "Java"
  },
  "publishedAt": "2026-02-01 12:00:00",
  "updatedAt": "2026-02-01 12:00:00"
}

```

Admin post list item

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Post id|
|title|string|false|none|Post title|
|status|string|false|none|Status|
|category|[CategoryVO](#schemacategoryvo)|false|none|Category|
|publishedAt|string|false|none|Published time|
|updatedAt|string|false|none|Updated time|

<h2 id="tocS_ApiResponsePageResultAdminPostListItemVO">ApiResponsePageResultAdminPostListItemVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresultadminpostlistitemvo"></a>
<a id="schema_ApiResponsePageResultAdminPostListItemVO"></a>
<a id="tocSapiresponsepageresultadminpostlistitemvo"></a>
<a id="tocsapiresponsepageresultadminpostlistitemvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "Hello World",
        "status": "PUBLISHED",
        "category": {
          "id": 1,
          "name": "Java"
        },
        "publishedAt": "2026-02-01 12:00:00",
        "updatedAt": "2026-02-01 12:00:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[PageResultAdminPostListItemVO](#schemapageresultadminpostlistitemvo)|false|none|Pagination result wrapper|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_PageResultAdminPostListItemVO">PageResultAdminPostListItemVO</h2>
<!-- backwards compatibility -->
<a id="schemapageresultadminpostlistitemvo"></a>
<a id="schema_PageResultAdminPostListItemVO"></a>
<a id="tocSpageresultadminpostlistitemvo"></a>
<a id="tocspageresultadminpostlistitemvo"></a>

```json
{
  "list": [
    {
      "id": 1,
      "title": "Hello World",
      "status": "PUBLISHED",
      "category": {
        "id": 1,
        "name": "Java"
      },
      "publishedAt": "2026-02-01 12:00:00",
      "updatedAt": "2026-02-01 12:00:00"
    }
  ],
  "total": 100,
  "pageNum": 1,
  "pageSize": 10
}

```

Pagination result wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|list|[[AdminPostListItemVO](#schemaadminpostlistitemvo)]|false|none|Current page list|
|total|integer(int64)|false|none|Total items count|
|pageNum|integer(int64)|false|none|Current page number|
|pageSize|integer(int64)|false|none|Page size|

<h2 id="tocS_AdminPostEditVO">AdminPostEditVO</h2>
<!-- backwards compatibility -->
<a id="schemaadminposteditvo"></a>
<a id="schema_AdminPostEditVO"></a>
<a id="tocSadminposteditvo"></a>
<a id="tocsadminposteditvo"></a>

```json
{
  "id": 1,
  "title": "Hello World",
  "summary": "Short introduction",
  "content": "# Heading\n\nContent...",
  "coverUrl": "/uploads/202602/cover.jpg",
  "categoryId": 1,
  "tagIds": [
    1,
    2,
    3
  ],
  "status": "DRAFT"
}

```

Admin post edit model

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Post id|
|title|string|false|none|Title|
|summary|string|false|none|Summary|
|content|string|false|none|Content in Markdown|
|coverUrl|string|false|none|Cover image URL|
|categoryId|integer(int64)|false|none|Category id|
|tagIds|[integer]|false|none|Tag ids|
|status|string|false|none|Status|

<h2 id="tocS_ApiResponseAdminPostEditVO">ApiResponseAdminPostEditVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseadminposteditvo"></a>
<a id="schema_ApiResponseAdminPostEditVO"></a>
<a id="tocSapiresponseadminposteditvo"></a>
<a id="tocsapiresponseadminposteditvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1,
    "title": "Hello World",
    "summary": "Short introduction",
    "content": "# Heading\n\nContent...",
    "coverUrl": "/uploads/202602/cover.jpg",
    "categoryId": 1,
    "tagIds": [
      1,
      2,
      3
    ],
    "status": "DRAFT"
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[AdminPostEditVO](#schemaadminposteditvo)|false|none|Admin post edit model|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_AdminDashboardStatsVO">AdminDashboardStatsVO</h2>
<!-- backwards compatibility -->
<a id="schemaadmindashboardstatsvo"></a>
<a id="schema_AdminDashboardStatsVO"></a>
<a id="tocSadmindashboardstatsvo"></a>
<a id="tocsadmindashboardstatsvo"></a>

```json
{
  "total": 10,
  "draft": 2,
  "published": 8,
  "categories": 5,
  "tags": 12,
  "commentsPending": 3,
  "totalViews": 12345
}

```

Dashboard stats

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|total|integer(int64)|false|none|Total posts|
|draft|integer(int64)|false|none|Draft posts|
|published|integer(int64)|false|none|Published posts|
|categories|integer(int64)|false|none|Categories count|
|tags|integer(int64)|false|none|Tags count|
|commentsPending|integer(int64)|false|none|Pending comments count|
|totalViews|integer(int64)|false|none|Total views of all posts|

<h2 id="tocS_ApiResponseAdminDashboardStatsVO">ApiResponseAdminDashboardStatsVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseadmindashboardstatsvo"></a>
<a id="schema_ApiResponseAdminDashboardStatsVO"></a>
<a id="tocSapiresponseadmindashboardstatsvo"></a>
<a id="tocsapiresponseadmindashboardstatsvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "total": 10,
    "draft": 2,
    "published": 8,
    "categories": 5,
    "tags": 12,
    "commentsPending": 3,
    "totalViews": 12345
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[AdminDashboardStatsVO](#schemaadmindashboardstatsvo)|false|none|Dashboard stats|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_AdminCommentQuery">AdminCommentQuery</h2>
<!-- backwards compatibility -->
<a id="schemaadmincommentquery"></a>
<a id="schema_AdminCommentQuery"></a>
<a id="tocSadmincommentquery"></a>
<a id="tocsadmincommentquery"></a>

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "status": "PENDING",
  "postId": 1
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|pageNum|integer(int64)|false|none|Page number (start from 1)|
|pageSize|integer(int64)|false|none|Page size (1~100)|
|status|string|false|none|Comment status (optional): PENDING/APPROVED/REJECTED|
|postId|integer(int64)|false|none|Filter by postId|

<h2 id="tocS_AdminCommentVO">AdminCommentVO</h2>
<!-- backwards compatibility -->
<a id="schemaadmincommentvo"></a>
<a id="schema_AdminCommentVO"></a>
<a id="tocSadmincommentvo"></a>
<a id="tocsadmincommentvo"></a>

```json
{
  "id": 1,
  "postId": 1,
  "postTitle": "Hello World",
  "nickname": "Tom",
  "email": "tom@example.com",
  "content": "Nice post!",
  "status": "PENDING",
  "createdAt": "2026-02-01 12:00:00"
}

```

Admin comment item

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Comment id|
|postId|integer(int64)|false|none|Post id|
|postTitle|string|false|none|Post title|
|nickname|string|false|none|Nickname|
|email|string|false|none|Email|
|content|string|false|none|Content|
|status|string|false|none|Status|
|createdAt|string|false|none|Created time|

<h2 id="tocS_ApiResponsePageResultAdminCommentVO">ApiResponsePageResultAdminCommentVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponsepageresultadmincommentvo"></a>
<a id="schema_ApiResponsePageResultAdminCommentVO"></a>
<a id="tocSapiresponsepageresultadmincommentvo"></a>
<a id="tocsapiresponsepageresultadmincommentvo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "postId": 1,
        "postTitle": "Hello World",
        "nickname": "Tom",
        "email": "tom@example.com",
        "content": "Nice post!",
        "status": "PENDING",
        "createdAt": "2026-02-01 12:00:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[PageResultAdminCommentVO](#schemapageresultadmincommentvo)|false|none|Pagination result wrapper|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_PageResultAdminCommentVO">PageResultAdminCommentVO</h2>
<!-- backwards compatibility -->
<a id="schemapageresultadmincommentvo"></a>
<a id="schema_PageResultAdminCommentVO"></a>
<a id="tocSpageresultadmincommentvo"></a>
<a id="tocspageresultadmincommentvo"></a>

```json
{
  "list": [
    {
      "id": 1,
      "postId": 1,
      "postTitle": "Hello World",
      "nickname": "Tom",
      "email": "tom@example.com",
      "content": "Nice post!",
      "status": "PENDING",
      "createdAt": "2026-02-01 12:00:00"
    }
  ],
  "total": 100,
  "pageNum": 1,
  "pageSize": 10
}

```

Pagination result wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|list|[[AdminCommentVO](#schemaadmincommentvo)]|false|none|Current page list|
|total|integer(int64)|false|none|Total items count|
|pageNum|integer(int64)|false|none|Current page number|
|pageSize|integer(int64)|false|none|Page size|

<h2 id="tocS_AdminMeResponse">AdminMeResponse</h2>
<!-- backwards compatibility -->
<a id="schemaadminmeresponse"></a>
<a id="schema_AdminMeResponse"></a>
<a id="tocSadminmeresponse"></a>
<a id="tocsadminmeresponse"></a>

```json
{
  "id": 1,
  "username": "admin",
  "displayName": "Administrator"
}

```

Current admin profile

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Admin id|
|username|string|false|none|Username|
|displayName|string|false|none|Display name|

<h2 id="tocS_ApiResponseAdminMeResponse">ApiResponseAdminMeResponse</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponseadminmeresponse"></a>
<a id="schema_ApiResponseAdminMeResponse"></a>
<a id="tocSapiresponseadminmeresponse"></a>
<a id="tocsapiresponseadminmeresponse"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1,
    "username": "admin",
    "displayName": "Administrator"
  },
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[AdminMeResponse](#schemaadminmeresponse)|false|none|Current admin profile|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

<h2 id="tocS_AdminUserVO">AdminUserVO</h2>
<!-- backwards compatibility -->
<a id="schemaadminuservo"></a>
<a id="schema_AdminUserVO"></a>
<a id="tocSadminuservo"></a>
<a id="tocsadminuservo"></a>

```json
{
  "id": 1,
  "username": "admin",
  "displayName": "Administrator",
  "status": 1,
  "createdAt": "2026-02-01 12:00:00",
  "updatedAt": "2026-02-01 12:00:00"
}

```

Admin user

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|false|none|Admin id|
|username|string|false|none|Username|
|displayName|string|false|none|Display name|
|status|integer(int32)|false|none|Status: 1=enabled, 0=disabled|
|createdAt|string|false|none|Created time|
|updatedAt|string|false|none|Updated time|

<h2 id="tocS_ApiResponseListAdminUserVO">ApiResponseListAdminUserVO</h2>
<!-- backwards compatibility -->
<a id="schemaapiresponselistadminuservo"></a>
<a id="schema_ApiResponseListAdminUserVO"></a>
<a id="tocSapiresponselistadminuservo"></a>
<a id="tocsapiresponselistadminuservo"></a>

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "username": "admin",
      "displayName": "Administrator",
      "status": 1,
      "createdAt": "2026-02-01 12:00:00",
      "updatedAt": "2026-02-01 12:00:00"
    }
  ],
  "timestamp": 1700000000000
}

```

Standard API response wrapper

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|code|integer(int32)|false|none|Business status code. 0 means ok, non-zero means error.|
|message|string|false|none|Message for humans|
|data|[[AdminUserVO](#schemaadminuservo)]|false|none|Response data payload|
|timestamp|integer(int64)|false|none|Server timestamp in milliseconds|

