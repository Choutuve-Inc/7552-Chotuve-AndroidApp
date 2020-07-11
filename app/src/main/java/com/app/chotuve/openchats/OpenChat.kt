package com.app.chotuve.openchats

import com.app.chotuve.friendlist.Friend

class OpenChat(val text: String, var friend: Friend, val timestamp: Long) {
    constructor() : this( "", Friend(),  -1)
}