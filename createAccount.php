<?php

// Récupérer les informations de l'utilisateur depuis la requête POST
$username = $_POST['username'];
$password = $_POST['password'];

// Vérifier si les champs sont vides
if (empty($username) || empty($password)) {
    // Les champs sont vides, renvoyer une réponse d'échec
    echo "empty_fields";
} else {
    // Effectuer la validation des données si nécessaire

    // Se connecter à la base de données MySQL
    $servername = "127.0.0.1"; // Remplacez par le nom de votre serveur MySQL
    $dbname = "myloto"; // Remplacez par le nom de votre base de données MySQL
    $usernameDB = "root"; // Remplacez par le nom d'utilisateur de votre base de données MySQL
    $passwordDB = "90909090"; // Remplacez par le mot de passe de votre base de données MySQL

    try {
        $conn = new PDO("mysql:host=$servername;dbname=$dbname", $usernameDB, $passwordDB);
        $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // Vérifier si l'utilisateur existe déjà dans la base de données
        $stmt = $conn->prepare("SELECT * FROM users WHERE username = :username");
        $stmt->bindParam(':username', $username);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            // L'utilisateur existe déjà, renvoyer une réponse d'échec
            echo "failure";
        } else {
            // L'utilisateur n'existe pas, procéder à la création du compte

            // Hasher le mot de passe
            $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

            $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (:username, :password)");
            $stmt->bindParam(':username', $username);
            $stmt->bindParam(':password', $hashedPassword);
            $stmt->execute();

            // Compte créé avec succès, renvoyer une réponse de succès
            echo "success";
        }
    } catch(PDOException $e) {
        // Erreur lors de la connexion à la base de données
        echo "error";
    }

    $conn = null; // Fermer la connexion à la base de données
}

?>
