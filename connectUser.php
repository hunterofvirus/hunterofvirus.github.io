<?php
// Connexion à la base de données
$host = "localhost";
$username = "root";
$password = "90909090";
$database = "myloto";

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    die("Erreur de connexion à la base de données : " . $conn->connect_error);
}

// Récupérer les informations de connexion envoyées par l'application Android
$username = $_POST['username'];
$password = $_POST['password'];

// Vérifier si les champs sont vides
if (empty($username) || empty($password)) {
    // Les champs sont vides, renvoyer une réponse d'échec
    echo "empty_fields";
} else {
    // Échapper les valeurs pour éviter les injections SQL
    $username = $conn->real_escape_string($username);
    $password = $conn->real_escape_string($password);

    // Requête pour vérifier les informations de connexion dans la base de données
    $sql = "SELECT * FROM users WHERE username = '$username' AND password = '$password'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        // Informations de connexion valides
        echo "success";
    } else {
        // Informations de connexion invalides
        echo "failure";
    }
}

// Fermer la connexion à la base de données
$conn->close();
?>
