# calindar

![](http://i.imgur.com/a8OU84y.jpg)

Calindar is the second contribution to the cURL webapp takeover.

What does it do? Well, currently it allows you to ```POST```-define 'todos' - really, really skeletal events, meetings, reminders, notes, 
whatever you need - which just get thrown in a database.
This is all accomplished by throwing a ```POST``` to the ```/add``` endpoint, with a JSON-encoded map of params (or query-string params) that looks something like this:

```json
{"name":"Rewind and return space-jam to blockbster",
 "description":"The game seemed sorta rigged, no need to rewatch it",
 "time":"16:00",
 "date":"2017-02-26",
 "recur":false}
```

These then get pulled out and sorted as a defacto todo-list on a ```GET /today```, and built as an org-mode file in ```resources/org-files/{{date}}.org``` if you're into emacs.

![](resources/public/calendar_terminal.png)

## Prerequisites

Fire up a Postgresql db named `todo`, and you're good to go; the main and ring-servers run a migration on the table before they start.

## Running

To start a web server for the application, run:

    lein ring server-headless

## Future Features

- Deleting stuff might be nice
- `recur` is supposed to pre-map the event a handful of times in the future so you don't have to manually put in 'team standup at 2:30' everyday, but it doesn't do that yet
- 

## License

Copyright © 2017
