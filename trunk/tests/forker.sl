sub check
{
   return;
}

debug(15);

wait(fork({
   sleep(1000);
   check("within fork");
}), 5000);

check("outside of fork");
