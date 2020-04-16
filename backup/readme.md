# Backup and Restore

## How-to Backup

You can backup all at once:
```bash
TODO
```

Or just a single user:
```bash
TODO
```

## How-to Restore

The restore will not override existing data. It just adds unknown data!  

```bash
curl --location --request POST 'https://afrika-run.appspot.com/rest/backup/import' \
-u '<user>:<password>' \
--header 'Content-Type: application/json' \
--data-raw '{
  "users": [
    {
      "id": "xxxLEgRVc2VyGICAgxxx",
      "username": "andi",
      "password": "$2a$12$837oYjuU...BiGg",
      "roles": [
        "USER"
      ],
      "nickname": "Andi",
      "email": "andi@foo.bar",
      "benachrichtigunsIntervall": "taeglich",
      "includeMeInStatisticMail": false
    }
  ],
  "aktivitaeten": [
    {
      "id": "xxxXRhZXQYgICAgJxxx",
      "distanzInKilometer": 3.18,
      "typ": "radfahren",
      "aktivitaetsDatum": "2015-07-27T00:00:00.000+0000",
      "eingabeDatum": "2015-07-27T12:30:46.322+0000",
      "aufzeichnungsart": "aufgezeichnet",
      "bezeichnung": "foo",
      "owner": "andi"
    }
  ]
}'
```