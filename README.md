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
   
# Solution

## Data Structure Design

Data structure design was driven by the requested asymptotic complexities. Given the need to achieve O(log n) for add and remove operations, O(1) for size and get, and as fast as possible in the range queries, it was determined to use a combination of two complementing data structures. To achieve O(1) in get, we need a direct (or semi-direct) addressing structure and after considering the cardinality (in the range of 0 to 2^31) it requires the use of a hash function to transform the keys to a discret range. For this reason as first data structure was selected a hash map.

With the goal to provide the best possible version for GETRANGE, it was decided to use a search tree, where the key of each node will be the score value, and contains a list for the keys containing that value. The implementation details of the structure has to guarantee that insert and remove operations in O(log n), and execution time logaritmic for the worst case scenarios. The structures capable to guarantee those properties are balanced trees, where it's enforced that the heith (h) is logaritmic with respect to the number of nodes. Some examples of those strucutures are red-black trees (h=2log(n+1)), AVL, 2-3 trees, B-tries, k-neigbours trees and others.

In this context is important to mention, it was also studied the use of van Emde Boas trees. This data structure is capable to perform all the basic operatios in O(log log u), given the keys are in the range from 0 to u. In the problem at hand where the range of the keys is big, this approach produce an use of memory close to 8GB to store. Considering the lack of specific information about the statistics related with the distribution of the elements, we assume the number of elements to store is going to be in practice considerably smaller than the potential universe of keys.

After studing the state of the art in research regarding dynamic range search, several sudies were found. Between them we need to highligth the work "On Dynamic Range Reporting in One Dimension" giving a perspective on the status of the field. In this work the authors propose a data structure to guarantee O(log log n) for searchs, at the same time they keep logaritmic times for updates and their space requirements are O(n), being n the number of elements to store. However, some of the implementation details are not clear in the work. For these reasons to provide the solution at this problem it was chosen to use as base for the implementation the theory behind red-black trees.


