<?php

$host = "db.tecnico.ulisboa.pt";
$user = "ist425186";
$password = "svkz6085";
$dbname = $user;

$db = new PDO("pgsql:host=$host;dbname=$dbname", $user, $password);
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);