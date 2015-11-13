open FILEOUT, ">pictures.txt" or die $!; #the file in which to write the picture URLs

#write the source of the URL to the file htmlSource.txt
system('wget -O htmlSource.txt --no-check-certificate -i /etc/init.d/preglow-pages.txt -q' );

open HTML, "<htmlSource.txt" or die $!;

while (my $line = <HTML>) {
  if ($line =~ m/\/resource\/image/) { #if the line contains a URL, write it to the file
        my @words = split('&quot;', $line);
        foreach(@words) {
           if($_ =~ m/\/resource\/image/) {
                print FILEOUT  "https://preview.helios.@STUDIO_TLD@" . $_ . "\n";
           }
         }
   }
}
close(HTML);

#runs wget for each URL in pictures.txt, logs events to wget.log
system('cat pictures.txt | sort -u > pictures-unique.txt');
system('wget --no-check-certificate -O- >/dev/null -o wget.log -i pictures-unique.txt');

close(FILEOUT);