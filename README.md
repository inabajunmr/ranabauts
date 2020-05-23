# ranabauts
ranabauts is Test application for coordination between microservices.

## overview
a call with
```json
{
    "commands": [
        {
            "type": "HTTP",
            "uri": "http://b.example.com",
            "commands": [
                {
                    "type": "HTTP",
                    "uri": "http://c.example.com",
                    "response": {
                        "status": 200
                    }
                },
                {
                    "type": "HTTP",
                    "uri": "http://d.example.com",
                    "response": {
                        "status": 200
                    }
                }
            ],
            "response": {
                "status": 200
            }
        }
    ],
    "response": {
        "status": 200
    }
}
```

1. a call b
2. b call c
3. b call d

Command object has `next` and `response` and `commands`.
If `commands` is null, API just return response.
If `commands` is not null, api execute command.

## HTTP command sample
Call `url` with POST method and commands body.

When this command call like follow example, http body is `commands` array.

### request
```json
{
    "commands": [
        {
            "type": "HTTP",
            "uri": "http://b.example.com",
            "commands": [
                {
                    "type": "HTTP",
                    "uri": "http://c.example.com",
                    "response": {
                        "status": 200
                    }
                },
                {
                    "type": "HTTP",
                    "uri": "http://d.example.com",
                    "response": {
                        "status": 200
                    }
                }
            ],
            "response": {
                "status": 200
            }
        }
    ],
    "response": {
        "status": 200
    }
}
```

### b request body
```json
{
    "type": "HTTP",
    "uri": "http://b.example.com",
    "commands": [
        {
            "type": "HTTP",
            "uri": "http://c.example.com",
            "response": {
                "status": 200
            }
        },
        {
            "type": "HTTP",
            "uri": "http://d.example.com",
            "response": {
                "status": 200
            }
        }
    ],
    "response": {
        "status": 200
    }
}
```

b execute each commands.

### c request body
```json
{
    "type": "HTTP",
    "uri": "http://c.example.com",
    "response": {
        "status": 200
    }
}
```

c don't has command so just return response.

### d request body
```json
{
    "type": "HTTP",
    "uri": "http://d.example.com",
    "response": {
        "status": 200
    }
}
```

d don't has command so just return response.

