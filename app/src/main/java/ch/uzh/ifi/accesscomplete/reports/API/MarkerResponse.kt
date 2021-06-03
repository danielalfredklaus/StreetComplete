package ch.uzh.ifi.accesscomplete.reports.API

import androidx.room.PrimaryKey

data class MarkerResponse(
    @PrimaryKey val mid: String
)
/*
{
    "location": {
    "coordinates": [
    69,
    69
    ],
    "geo_type": "point"
},
    "verifier_count": 0,
    "isActive": true,
    "mid": "24ZQpENq8pHkUqWdEuUQB",
    "title": "test",
    "subtitle": "test",
    "image_url": "yamum",
    "tags": [
    {
        "_id": "60b7be5458cafd000e420cf2",
        "k": "test",
        "v": "test"
    },
    {
        "_id": "60b7be5458cafd000e420cf3",
        "k": "test2",
        "v": "test2"
    }
    ],
    "description": "string",
    "updatedby": "fF68PQWq2PH2Pv-",
    "verifiers": [],
    "history": [],
    "createdon": "2021-06-02T17:22:28.726Z"
}
*/
