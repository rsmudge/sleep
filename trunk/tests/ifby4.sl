#
# weird bug I managed to create with multiple ||'s and &&'s
# this is to prevent such a stupid mistake again
#
$text = '/theme /Use';

if ($text ismatch '/dcc send \S+ (.+)' ||
            $text ismatch '/send \S+ (.+)' ||
            $text ismatch '/load (.+)' ||
            $text ismatch '/theme (.+)' ||
            $_IS_FILE_COMPLETE == 1)
{
      println("We do have a match, what happens next, I don't know...");
}

