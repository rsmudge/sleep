Prior to serialization:
@('a', 'b', 'c', 'd', 'e')
@('c', 'd')
Post serialization
@('a', 'b', 'c', 'd', 'e')
@('c', 'd')
The push!
@('a', 'b', 'c', 'd', 'e')
@('c', 'd', 'hello world!')
And for the originals
@('a', 'b', 'c', 'd', 'e')
@('c', 'd')
