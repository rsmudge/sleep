#!/usr/bin/perl

@files = `find . | grep java`;
chomp(@files);


$t[0] = "#lines";
$var  = "Filename";
$count = "count";

write STDOUT;

$count = 0;

foreach $var (@files)
{
   $c = `wc -l $var`;
   $c = join("", split(/ /, $c));
   @t = split(/\./, $c);
   $count += $t[0];
   write STDOUT;
}

print $count."\n";

format STDOUT =
@<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<   @<<<<<   @<<<<<<<
$var $t[0] $count
.
