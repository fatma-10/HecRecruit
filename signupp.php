<?php
mysql_connect("Localhost", "root");
mysql_select_db("covoiturage") or die('E1: ' . mysql_error());

$fn=$_post["fn"];
$ln=$_post["ln"];
$email=$_post["email"];
$tel=$_post["tel"];
$pwd=$_post["pwd"];
$adr=$_post["adr"];
?>