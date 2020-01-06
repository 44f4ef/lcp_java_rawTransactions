# java based tools for LCP network
## Necessary libraries
## 必要的代码库
[commons-codec-1.13.jar](https://commons.apache.org/proper/commons-codec/download_codec.cgi)

[bcprov-jdk15to18-164.jar](https://www.bouncycastle.org/latest_releases.html)

to make an address
做一个地址:

**public keys must be base64 encoded strings.**

公钥必须是base64编码的字符串。

## wallet address (钱包地址)


```LCP.Addresses.generateAddress(releaseCondition)``` 

  **releaseCondition** must be an **Object** array in this form:
    
   `["sig",{"pubkey":"Ald9tkgiUZQQ1djpZgv2ez7xf1ZvYAsTLhudhvn0931w"}]`


## Device Address 设备地址:

`LCP.Addresses.generateDeviceAddress(publicKey)`

   *publicKey* must be in this form base64 encoded *String*:
     
    `"Ald9tkgiUZQQ1djpZgv2ez7xf1ZvYAsTLhudhvn0931w"`
     
## Create Raw Transactions

`makeRaw(JSONObject[] outputs,
         JSONObject[] inputs,
         JSONObject[] authors,
         JSONObject headerInfo)`

### outputs:
```
    [
    
    {"amount":17396604,
    
    "address":"PGOFRZWHPNIC5EKU2P2CN2AQVTME34CD"}, 
    
    ... ]
  ```                       
    
   *amount* - the number coins being sent to the destination *address*.
    
   *address* - the 32 character address of the recipient.
    
    
  ### inputs:
  ```
  [
  {"unit":"0pzRg8DT1sfS95fbv3XK7OOjIDw4B/QcWXOPYectSJg=",
  
  "message_index":0,
  
  "output_index":0},
  
  ... ]
  ```
  
  *unit* - the hash of the source unit where the coins come from. 
  
  *message_index* - a unit can have multiple messages in the message array so 
  this index selects one of possible many but normally there is only one message.
  
  *output_index* - a *payment* message can send coins to mulitple addresses. what
  is the index of your address in that output array.
  
  ### authors
  
  
  

