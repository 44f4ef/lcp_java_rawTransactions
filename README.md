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
**simple**
**简单**

```LCP.Addresses.makeAddressFromPubKey(publicKeyBase64)``` 

**not simple**
**不简单**

addresses can be made from address keys. address keys are made from wallet keys 
directly or from master key mnemonic and password.

地址可以通过地址密钥进行

`DeterministicKey addressKey = KeyManagement.deriveAddressKey(walletKey);`

or 

`DeterministicKey addressKey = KeyManagement.deriveAddressKey(mnemonic, password);`

this is how to generate an address from the address key:

这是从抵制迷药生成地址的方法：

```
String pubKeyB64 = LCP.KeyManagement.getPublicKeyBase64(addressKey,[change],[address_index]);
String address = LCP.Addresses.makeAddressFromPubKey(pubKeyB64);
```

     
## Create Raw Transactions

## 创建原始交易


```
Transaction transaction = new Transaction(JSONObject[] outputs,
           JSONObject[] inputs,JSONObject[] signers,JSONObject networkInfo);
```

### outputs:
```
 [
    
    {"amount":17396604,
    
    "address":"PGOFRZWHPNIC5EKU2P2CN2AQVTME34CD"}, 
    
    ... 

]
  ```                       
    
   *amount* - the number coins being sent to the destination *address*.
    
   *address* - the 32 character address of the recipient.
    
    
  ### inputs:
  ```
[
            {"unit":"0pzRg8DT1sfS95fbv3XK7OOjIDw4B/QcWXOPYectSJg=",
  
             "message_index":0,
  
             "output_index":0},
 
  ... 

]
  ```
  
  *unit* - the hash of the source unit where the coins come from. 
  
  *message_index* - a unit can have multiple messages in the message array so 
  this index selects one of possible many but normally there is only one message.
  
  *output_index* - a **payment** message can send coins to mulitple addresses. what
  is the index of your address in that output array. most times that output_index is
  1 because the first index would usually be the senders change address.
  
  ### signers
  the address of the signer(s) of the transaction. this is the address(s) of the coins
  that the user is trying to unlock
  ```
"authors":[
              {
                 "address":"5PNCW2VHMOSGRQYJW7WQ7JVTX3HY5ZDB",
         
              },

            ...

           ]
```
## network info is usually gotten from the network.
use the python lcp server to do that with

```curl http://localhost:8080/get_header_info```

the result is:

```
  {
    'parent_units': ['RNlEq0wwg2ipYAP8c6suc9cDBgR9mrxa1lbVYRgXMGk='], 
    'last_stable_mc_ball': '+/RD5iwLqjXKiEp0yphNZ0rcFx1qF4ewS5zb92ZZg5A=', 
    'last_stable_mc_ball_unit': 'UHrq5UjL/9J0Y+vwyWEMv/kbN60lzvJtghacgQJ2Zoc=', 
    'last_stable_mc_ball_mci': 1562561, 
    'witness_list_unit': 'FxtHGkOHjv7aXajkg00/22Xp7VVXZEvT5cXfe8BSZEQ='
  }
```
  

## To Sign
```
Transaction transaction = new Transaction(....);

String signedUnit = transaction.sign(DeterministicKey signerKey);

```


## Unit

a completed transaction will appear like this:

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
