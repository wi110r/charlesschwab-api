<h2>INSTRUCTIONS...</h2>

<h3>CREATE AUTH JSON</h3>

Please create a json file with the following information and copy the path...
(This includes the app-key and app-secret found in your app settings
on charles schwab developers website. All other fields should be as shown below.)


```json
{
  "key": "your-key",
  "secret": "your-secret",
  "accountNumber": "",
  "actNumberHashValue": "",
  "refresh_token": "",
  "access_token": "",
  "id_token": "",
  "accessTokenExpiryInMs": 0,
  "refreshTokenExpiryInMs": 0
}
```


<h3>LOGIN - Once per week.</h3>

```kotlin
CsApi.buildApi("Path\\toyourauthjsonfile.json")

CsApi.getApi().login()
```


Follow login instructions in output to login.
Once a blank webpage is loaded, copy the url and paste into console and press ENTER.
The access token, refresh token, and account keys will be saved to your json file path.
You will need to login like this once per week.


<h3>API is ready to use...</h3>

```kotlin
CsApi.buildApi("Path\\toyourauthjsonfile.json")

val csapi = CsApi.getApi()

println(csapi.getStockQuote("AAPL"))
```


<h3>NOTES...</h3>

All account related requests require the hash value of the account number


