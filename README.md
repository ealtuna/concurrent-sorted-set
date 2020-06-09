# Concurrent Sorted Set Specification

In many systems there is a need to keep track of a sorted structure. This data structure comes out-of-the-box in the popular open-source system Redis. However, Redis is explicitly designed to be single-threaded in order to keep the code and system simple. Given the amount of data and access patterns for retrieving it later, it's needed faster version that is able to support the same sorted set abstraction.

The goal of this task is to implemented a networked sorted set that handles positive integers. The program should listen on a Unix domain socket at "./socket", accept new connections from multiple clients, and process commands that are sent over these connections via the binary protocol described below. The program should be running, even after all connections have been disconnected, until it is terminated. Each <...> represents a four byte unsigned integer in network byte order* sent or received on the socket. All client -> server and server -> client commands are prefixed with the number of fields in the command (detailed below). All set ids, keys, and scores will be positive.

The server should implement the following commands:

Add Score: Adds member <key> to <set>, with score <score>. If <set> doesn't exist, it's created. If <key> is already in <set>, its score is updated. Should run in time O(log(size(<set>))).

    Client: <4> <1> <set> <key> <score>
    Server: <0>

Remove Key: Removes <key> from <set> if <set> exists and <key> is in <set>. Should run in time O(log(size(<set>))).

    Client: <3> <2> <set> <key>
    Server: <0>

Get Size: Returns the size of set <set>, or 0 if <set> doesn't exist. Should run in time O(1).

    Client: <2> <3> <set>
    Server: <1> <size>

Get key-value: Returns the score of key <key> in <set>, and 0 if either the set does not exist or does not contain <key>. Should run in time O(1).

    Client: <3> <4> <set> <key>
    Server: <1> <score>

Get Range: Returns all elements in sets <set1> ... <setM> with scores in the range [<lower>, <upper>]. Elements should be returned sorted by non-decreasing order of key. If two keys match, the elements with matching keys should be sorted by non-decreasing order of value. This is the most important operation, you should achieve the best asymptotic complexity you can.

    Client: <N> <5> <set1> ... <setM> <0> <lower> <upper>
    Server: <K> [<key> <score>] (repeat for each element of the set returned, where K is the total number of integers returned)

DISCONNECT:

    Client: <1> <6>
    Server: No response, then disconnect the client
   
