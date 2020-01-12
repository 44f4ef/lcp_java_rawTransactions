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

`LCP.Transactions.makeRaw(JSONObject[] outputs,
         JSONObject[] inputs,
         JSONObject[] authors,
         JSONObject headerInfo)`

### outputs:
```
   outputs: [
    
    {"amount":17396604,
    
    "address":"PGOFRZWHPNIC5EKU2P2CN2AQVTME34CD"}, 
    
    ... 

]
  ```                       
    
   *amount* - the number coins being sent to the destination *address*.
    
   *address* - the 32 character address of the recipient.
    
    
  ### inputs:
  ```
  inputs: [
            {"unit":"0pzRg8DT1sfS95fbv3XK7OOjIDw4B/QcWXOPYectSJg=",
  
             "message_index":0,
  
             "output_index":0},
 
  ... 

]
  ```
  
  *unit* - the hash of the source unit where the coins come from. 
  
  *message_index* - a unit can have multiple messages in the message array so 
  this index selects one of possible many but normally there is only one message.
  
  *output_index* - a *payment* message can send coins to mulitple addresses. what
  is the index of your address in that output array.
  
  ### authors
  the signer(s) of the releaseCondition.
  ```
"authors":[
              {
                 "address":"5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB",
                 "authentifiers":{
                    "r":"yPSkwrAQB0gZhN7JsYGb18+xaDcTWdnJQTkQTOl0A1U65wEKM+u172iBWJxQq+TDNhiNnzEQ6kea4QUEbt3TmA=="
                 }
              },

            ...

           ]
```


addresses are just hashes of **Relase Conditions** and **Release Conditions** 
are minimally necessary conditions to unlock coins. 99.9 percent of the time 
a release condition is just signature derived from signing this:

```
{ version: '1.0',

  alt: '1',

  witness_list_unit: 'FxtHGkOHjv7aXajkg00/22Xp7VVXZEvT5cXfe8BSZEQ=',

  last_ball_unit: '7yjsGAO3C1s5bmXePNy+JLD5XJvxX2BFmN3HpCYWp0w=',

  last_ball: 'MHbriaJFqc2Wxj/UBWSOk6EpLNlnfe71aXWsOdVMz8U=',

  parent_units: [ '0O9uvGcF/sbVnTdfZkbfJcJVuQvVE6aIWk2f7SDZZnA=' ],

  authors: 
       [ { address: '5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB'} ],

  messages: 
       [ { app: 'payment',
           payload_hash: 'pXVXZVN5MRXhJZtHyGtadSu3DSnuKiUm8espRwBg7rs=',
           payload_location: 'inline' } ] 

}

```

this is functionally a header without **authentifiers** and **payloadless messages**

## Signatures
signatures should have a length of **exactly** 88 bytes. They should be a concatenation
of just the **r** and **s** points.


## headerInfo
header info comes from the network. 
  
## Unit

a completed transaction will appear liket this:

```
{
      "unit":"/XmIf5lnmZTTntKL0I0fQ8XavLfOn7jq7YC3nqCJ6GI=",
      "version":"1.0",
      "alt":"1",
      "witness_list_unit":"FxtHGkOHjv7aXajkg00/22Xp7VVXZEvT5cXfe8BSZEQ=",
      "last_ball_unit":"7yjsGAO3C1s5bmXePNy+JLD5XJvxX2BFmN3HpCYWp0w=",
      "last_ball":"MHbriaJFqc2Wxj/UBWSOk6EpLNlnfe71aXWsOdVMz8U=",
      "headers_commission":344,
      "payload_commission":157,
      "main_chain_index":980897,
      "timestamp":1578309540,
      "parent_units":[
         "0O9uvGcF/sbVnTdfZkbfJcJVuQvVE6aIWk2f7SDZZnA="
      ],
      "authors":[
         {
            "address":"5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB",
            "authentifiers":{
               "r":"yPSkwrAQB0gZhN7JsYGb18+xaDcTWdnJQTkQTOl0A1U65wEKM+u172iBWJxQq+TDNhiNnzEQ6kea4QUEbt3TmA=="
            }
         }
      ],
      "messages":[
         {
            "app":"payment",
            "payload_hash":"pXVXZVN5MRXhJZtHyGtadSu3DSnuKiUm8espRwBg7rs=",
            "payload_location":"inline",
            "payload":{
               "inputs":[
                  {
                     "unit":"OnpvoZpzsBcOd+zfshQhsSWF+HC7A20vpRfdgXpqZDc=",
                     "message_index":0,
                     "output_index":0
                  }
               ],
               "outputs":[
                  {
                     "address":"5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB",
                     "amount":24586996
                  }
               ]
            }
         }
      ]
   }
}

```
  

