#!/usr/bin/env bash

#######################################
#  Testing API using CURL commands    #
#######################################

################
#  Archers API #
################

## listAllArchers
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:dFW96LWgjJTKjTk8sZ9ki8nzmXqq8+VNaqtbOZ21tf4=" \
     https://localhost:7878/archers

## addArcher
curl -k -X "POST" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:dFW96LWgjJTKjTk8sZ9ki8nzmXqq8+VNaqtbOZ21tf4=" \
     --json '{"firstName":"TestUser2", "lastName":"IntTest2", "email":"test2@mail.org", "city":"NY", "country":"US", "clubName":"US Club"}' \
     https://localhost:7878/archers

## getArcherByID
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:U5PFSU2Oi2ImCMr4xf5qLqYRy50JSSEaQtG/5i8EtJg=" \
     https://localhost:7878/archers/1

## updateArcher
curl -k -X "PUT" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:iPqofFGEtuOyNpAC8MiwtpIjn5zB1jjIfdCBR1Zqrjo=" \
     --json '{"firstName":"TestUser", "lastName":"IntTest", "email":"test@mail.org"}' \
     https://localhost:7878/archers/2

## deleteArcherByID
curl -k -X "DELETE" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:iPqofFGEtuOyNpAC8MiwtpIjn5zB1jjIfdCBR1Zqrjo=" \
     https://localhost:7878/archers/2


##############
#  Bows API  #
##############

## listAllBows for archer
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:97PIboLIfrTaL9zIdxzBumMVGyNihQIAs93KQROwlwo=" \
     https://localhost:7878/archers/1/bows

## addBow
curl -k -X "POST" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:97PIboLIfrTaL9zIdxzBumMVGyNihQIAs93KQROwlwo=" \
     --json '{"name":"Test Bow", "type":"COMPOUND", "level":"BEGINNER", "poundage":"18-22", "compoundModel":"Beginner chip compound"}' \
     https://localhost:7878/archers/1/bows

## getBowByID
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:5PJfcQD2nO9CiWAtkeym/lYa8LWbIPU5wlhNyHoXd10=" \
     https://localhost:7878/archers/1/bows/1

## updateBow
curl -k -X "PUT" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:ud6bmb8n1dNi/2bWVt/DlOy/4LvAyBke1N5U2BUwLEk=" \
     --json '{"name":"Update Test Bow", "type":"COMPOUND", "level":"ADVANCED", "poundage":"28-32", "compoundModel":"Advanced expensive compound"}' \
     https://localhost:7878/archers/1/bows/6

## addDistanceSettings for bow
curl -k -X "PATCH" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:ud6bmb8n1dNi/2bWVt/DlOy/4LvAyBke1N5U2BUwLEk=" \
     --json '{"distance":"50", "sight":"92 & 3.0", "isTested":"true"}' \
     https://localhost:7878/archers/1/bows/6

## deleteBowByID
curl -k -X "DELETE" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:ud6bmb8n1dNi/2bWVt/DlOy/4LvAyBke1N5U2BUwLEk=" \
     https://localhost:7878/archers/1/bows/6


################
#  Rounds API  #
################

## listAllRounds for archer
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:H4OuMQxea1fmTdDOt1eiwUe7g1TmKhGnmaADML0RV7M=" \
     https://localhost:7878/archers/1/rounds

## listAllRounds for archer by page
## need to put URL inside quotes to make query parameters work, cause `&` can't be sent directly
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:lClB4npXQkWyuRdMiFWUI5Kx/YABeiwO8LLysbO4tjY=" \
     "https://localhost:7878/archers/1/rounds?page=0&size=5"

## addRound for archer
curl -k -X "POST" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:H4OuMQxea1fmTdDOt1eiwUe7g1TmKhGnmaADML0RV7M=" \
     -H 'Content-Type: application/json; charset=utf-8' \
     --data-raw '
     {
        "roundDate":"2025-01-01", "distance":"30", "targetFace":"122cm", "bowId":"1",
        "ends": [
           {
              "endNumber":"1",
              "shots": [
                  {"shotNumber":"1", "shotScore":"7"}
               ]
            }]
     }' \
     https://localhost:7878/archers/1/rounds

## getRoundByID
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:TnEQbZgooHS9aZJcfk0msd1yMq4NRMojcVKUiDQzjto=" \
     https://localhost:7878/archers/1/rounds/1

## deleteRoundByID
curl -k -X "DELETE" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:CSzUUIUnnCPr80xeVjm7yVR8coOJiu5H3iJWMFIW538=" \
     https://localhost:7878/archers/1/rounds/2

## get best rounds
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:HtPqbvMfww3noAKt5KaP+SkHKMdhERhWfMwHzrui0gk=" \
     https://localhost:7878/archers/1/rounds/statistics/best

## get total rounds
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:PM3mJuq8b0Zhyl4pCyibM2Mma1ZIhiZe7wFw/F1pgbo=" \
     https://localhost:7878/archers/1/rounds/statistics/total

## get total rounds for last month
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:U21s1VrgxsJsDM/vifk8gY3e08f40ewAg/dxizvcUog=" \
     https://localhost:7878/archers/1/rounds/statistics/total/lastMonth

######################
#  Competitions API  #
######################

## listAllCompetitions for archer
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:HJc6JGpfJthv/fZ8wDo61qcfNgt0wL1bh0TmDZpeJoM=" \
     https://localhost:7878/archers/1/competitions

## listAllCompetitions for archer by page
## need to put URL inside quotes to make query parameters work, cause `&` can't be sent directly
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:kATm2dgzqCIxuwDItk7qY3UiIHa+dqiPPC9sk2GVInk=" \
     "https://localhost:7878/archers/1/competitions?page=0&size=5"

## addCompetition for archer
curl -k -X "POST" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:HJc6JGpfJthv/fZ8wDo61qcfNgt0wL1bh0TmDZpeJoM=" \
     -H 'Content-Type: application/json; charset=utf-8' \
     --data-raw '
     {
        "competitionDate":"2025-03-20","competitionType":"Canadian 1200","ageClass":"45","comment":"int test","country":"US","city":"NY",
        "rounds":[
             {
                "roundDate":"2025-01-01", "distance":"30", "targetFace":"122cm", "bowId":"1",
                "ends": [
                   {
                      "endNumber":"1",
                      "shots": [
                          {"shotNumber":"1", "shotScore":"7"}
                      ]
                   }]
             }]
     }' \
     https://localhost:7878/archers/1/competitions

## getCompetitionByID
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:ro4zLSUu6gGFJbtu8BYHd65XLqoWndyZfh8FywUbLSo=" \
     https://localhost:7878/archers/1/competitions/1

## deleteCompetitionByID
curl -k -X "DELETE" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:TxL0Z2xhgum0059QzrCfKKoZThPwZbop2mhQMhxJCUg=" \
     https://localhost:7878/archers/1/competitions/2



################
#  Clubs API #
################

## listAllClubs
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:QfZP2lCYEofs3I8RgLjz8cFPGlCkgENT2ka0cJwpoYk=" \
     https://localhost:7878/clubs

## addClub
curl -k -X "POST" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:QfZP2lCYEofs3I8RgLjz8cFPGlCkgENT2ka0cJwpoYk=" \
     --json '{"name":"test club", "country":"test", "city":"int test"}' \
     https://localhost:7878/clubs

## getClubByID
curl -k -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:iCZ0G7eIRzP09IbhOwnvPn3n33MKbyCrXFnRuJC9Pq4=" \
     https://localhost:7878/clubs/1

## deleteClubByID
curl -k -X "DELETE" \
     -H "key:testAccessKey" -H "nonce:test" -H "timestamp:2025-01-01" \
     -H "signature:FJyVEbkTuZLhymuqFCSgNYy5Y14l0JcH0H8IJqqvyzI=" \
     https://localhost:7878/clubs/3

