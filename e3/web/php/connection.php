<?php

$host = "db.tecnico.ulisboa.pt";
$user = "ist424860";
$password = "cngg7415";
$dbname = $user;

$db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);