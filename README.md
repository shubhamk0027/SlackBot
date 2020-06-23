# SlackBot
This Slack bot is for the front end support for the [Mock Server](https://github.com/shubhamk0027/MockServer).
It automatically sends the queries to the MockServer. 

Run this slack server after adding bearer token and slack client token, signing secret and the address of the mockserver application.properties as 

    MOCK_SERVER_ADDRESS=localhost:8080
    SLACK_BOT_TOKEN=FULL_TOKEN_HERE
    SLACK_SIGNING_SECRET=SIGNING_SECRET_HERE
    
To connect with your slack account. Add all the 8 slash commands with publically accessible address of the bot (this slack server runs at localhost:3000 port, you can use ngork for making this port getting publically accessible) say https://someaddress.ngrok.io/slack/events to your own slack app. And set the request url in interactivity and shortcuts to https://someaddress.ngrok.io/slack/events. 

