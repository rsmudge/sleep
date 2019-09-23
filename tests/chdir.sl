#

chdir("data");
printAll(filter({ return iff("*.svn" !iswm $1, $1); }, sorta(ls())));

chdir("../data2");

if (!-exists "test.pl")
{
   println("So far so good...");
}

printAll(`ls -1`);
