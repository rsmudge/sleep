#
#
#

inline comment
{
   print('<!-- ');
   inline($1);
   println(' -->');
}

inline p
{
   print('<p>');
   inline($1);
   println('</p>');
}

inline form
{
   println('<form action="'.$1.'">');
   inline($2);
   println('</form>');
}

inline select
{
   println('  <select name="'.$1.'">');
   inline($2);
   println('  </select>');
}

inline option
{
   println('   <option value="'.$1.'">'.$2.'</option>');
}

sub makePage
{
   local('$title');
   $title = "Colors!!!";

   form("doit.cgi",
   {
      p(
      {
         print("Favorite color? ");

         select("colors",
         {
             pushl();
             local('$color $index');
             foreach $index => $color (@("red", "green", "blue", "orange"))
             {
                option($index, $color);
             }
             popl();

             comment({ warn(" this is just a test :) "); });
         });
      });

      p(
      { 
         print("Site title is: $title");
      });
   });
}

makePage();
