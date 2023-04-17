import kotlin.random.Random

/**
 * Кількість гравців
 * Програма не оброблює сітки з непарною кількістю гравців
 * Можливі значення 2, 4, 8, 16, 32, ...
 */
const val NUMBER_OF_PLAYER = 4

/**
 * База даних логінів та паролів усіх користувачів
 */
val users = registration()

/**
 * Реалізує гру
 */
fun main() {
    do {
        println(
            "Почати гру? Якщо так, пропишіть в консолі \"у\" "
        )
        val userInput = readln()
    } while (userInput != "y")
    startGame()
    println("Гра завершена.")
}

/**
 * Регістрація гравців
 * @return Мапа: ключ - логін гравця, значення - пароль.
 */
fun registration(): Map<String, String> {
    val users = mutableMapOf<String, String>()

    /**
     * Перевірка логіну на наяність у масиві users
     */
    fun checkLogin(login: String): Boolean {
        //перевірка всіх ключів у мапі
        for (key in users.keys) {
            if (login == key) {
                //співпадіння знайдено
                return true
            }
        }
        //співпадінь не знайдено
        return false
    }

    var i = 0
    while (i < NUMBER_OF_PLAYER) {
        println("Гравець ${i + 1} зареєструйтесь, будь ласка.")
        var login = createLogin()
        //перевірка на унікальність логіну
        while (checkLogin(login)) {
            println("!Данний логін вже використовується")
            login = createLogin()
        }
        println("Логін успішно встановлено. Ваш логін $login")
        val password = createPassword()
        println("Пароль успішно встановлено. Ваш пароль $password")
        //додати до мапи
        users[login] = password
        i++
    }
    return users
}

/**
 * Запросити логін у користувача та перевірити на відповідність вимогам
 * @return Логін користувача
 */
fun createLogin(): String {
    while (true) {
        print("Введіть логін: ")
        //приймаємо ввід від користувача
        val userInput = readln()

        //перевіряємо наявність літер у логіні
        if (!userInput.any { it.isLetter() }) {
            println("!Логін має містити хоча б одну букву")
            //перевіряємо наявність цифр у логіні
        } else if (!userInput.any { it.isDigit() }) {
            println("!Логін має містити хоча б одну цифру")
        } else {
            return userInput
        }
    }
}

/**
 * Запросити пароль у користувача та перевірити на відповідність вимогам
 * @return Пароль користувача
 */
fun createPassword(): String {
    while (true) {
        print("Введіть пароль: ")
        //приймаємо ввід від користувача
        val userInput = readln()

        //перевірка довжини пароля
        if (userInput.length < 8) {
            println("!Пароль має бути довшим за 8 символів")
            //перевіряємо наявність літер у паролі
        } else if (!userInput.any { it.isLetter() }) {
            println("!Пароль має містити хоча б одну букву")
            //перевіряємо наявність цифр у паролі
        } else if (!userInput.any { it.isDigit() }) {
            println("!Пароль має містити хоча б одну цифру")
        } else {
            return userInput
        }
    }
}

/**
 * Почати гру з зареєстрованими користувачами
 */
fun startGame() {
    println(
        "Починаємо гру! Учасники будуть розподілені на групи по 2 людини. " +
                "Спочатку відбудеться півфінал, після чого переможці будуть змагатися у фінальній грі"
    )

    //масив логінів
    val players = users.keys
    //перемішати масив
    var shuffledPlayers = players.shuffled()
    //кількість гравців у етапі сітки
    var playersInStage = NUMBER_OF_PLAYER
    var indexRound = 0
    while (playersInStage > 1) {
        //гравці, які перемогли у своїй парі
        val winners = mutableListOf<String>()
        var indexPlayer = 0
        var indexStage = 0
        //вивести в консоль пари
        showPairs(shuffledPlayers)
        //обіграш кожної пари етапу
        while (indexStage < playersInStage / 2) {
            val winner = startRound(indexRound, shuffledPlayers[indexPlayer], shuffledPlayers[indexPlayer + 1])
            indexStage++
            indexRound++
            indexPlayer += 2
            //додати гравця в масив
            winners.add(winner)
        }
        //перетасувати масив переможців
        shuffledPlayers = winners.shuffled()
        //гравців у кожному наступному етапі менше наполовину
        playersInStage /= 2
    }
    println("Переможець турніру: ${shuffledPlayers[0]}")
}

/**
 * Виводить пари у консоль
 */
fun showPairs(shuffledPlayers: List<String>) {
    val size = shuffledPlayers.size
    var indexPair = 0
    var indexPlayer = 0
    while (indexPair < size / 2) {
        println("Пара ${indexPair + 1} ")
        println(shuffledPlayers[indexPlayer])
        println(shuffledPlayers[indexPlayer + 1])
        indexPlayer += 2
        indexPair++
    }
}

/**
 * Починає раунд. Виконує підтвердження та авторизацію. Після чого гра починається
 * @param p1 Перший гравець
 * @param p2 Другий гравець
 * @return Нік переможця раунду
 */
fun startRound(round: Int, p1: String, p2: String): String {
    confirmRound(round + 1, p1, p2)
    authorization(p1)
    authorization(p2)
    println("гравці успішно авторизовані, починаємо гру!")
    return startPlay(p1, p2)
}

/**
 * Підтвердити початок раунду. В консоль виводиться повідомлення, після чого приймається ввід від користувача.
 * Повідомлення виводиться доти, доки в консоль не вписано "у"
 */
fun confirmRound(round: Int, p1: String, p2: String) {
    var message = round.toString()

    //окреме повідомлення для фінального раунду
    if (round == NUMBER_OF_PLAYER - 1) {
        message = "фінальний"
    }

    while (true) {
        println(
            "щоб почати $message раунд гравців пари $p1, $p2 , впишіть \"у\" в консолі. " +
                    "Далі Вам необхідно буде підтвердити свою особистість, ввівши пароль"
        )
        val userInput = readln()
        if (userInput == "y") {
            break
        }
    }
}

/**
 * Провести авторизацію гравця. Гравець має ввести свій пароль в консоль.
 * Перевірити введене значення на відповідність тому значенню, яке було введене при реєстрації
 */
fun authorization(player: String) {
    println("Користувач $player, введіть пароль, будь ласка")
    var userInput = readln()
    while (userInput != users.getValue(player)) {
        println("пароль вказаний невірно")
        userInput = readln()
    }
}

/**
 * Почати гру в хрестики нолики.
 * Цей метод описує основні механіки гри.
 * @return Нік переможця гри
 */
fun startPlay(p1: String, p2: String): String {
    //символи для гри
    //можна змінювати символи, але не кількість
    val chars = arrayListOf('X', '0')
    //визначаємо гравця, який буде робити перший хід(хрестиками)
    val firstMovePlayer = getRandomPlayer(arrayListOf(p1, p2))
    val secondMovePlayer = if (firstMovePlayer == p1) {
        p2
    } else {
        p1
    }
    println(firstMovePlayer + " - грає хрестиками\n" + secondMovePlayer + "- грає ноликами")
    val board = createBoard()
    //кількість зроблених кроків гравцями
    val numberOfMoves = Array(2) { 0 }
    val players = arrayListOf(firstMovePlayer, secondMovePlayer)

    /**
     * Перевірити чи задана клітинка зайнята. Якщо вільна повертає false
     */
    fun checkCell(cell: String): Boolean {
        return board[cell[0].toString().toInt() - 1][cell[1].toString().toInt() - 1] == chars[0].toString()
                || board[cell[0].toString().toInt() - 1][cell[1].toString().toInt() - 1] == chars[1].toString()
    }

    while (true) {
        //визначити гравця, який робить хід
        val index = (numberOfMoves[0] + numberOfMoves[1]) % 2
        //виводити стан поля після кожного ходу
        showBoard(board)
        println("Гравець ${players[index]}, будь ласка, оберіть та впишіть нижче код клітинки для свого ходу")

        //клітинка,яку обрав гравець
        var cell = makeMove()
        //поки користувач не введе вільну клітинку, питати код клітинки
        while (checkCell(cell)) {
            println("Обране Вами поле зайняте, спробуйте інше")
            cell = makeMove()
        }

        //замінити код клітинки на символ
        board[cell[0].toString().toInt() - 1][cell[1].toString().toInt() - 1] = chars[index].toString()
        numberOfMoves[index]++

        //перевірка на наявність переможця
        if (hasWinner(board)) {
            println("Гравець ${players[index]} виграв півфінальну гру за ${numberOfMoves[index]}")
            return players[index]
        }

        //всі поля заповнені, але жоден гравець не зібрав 3 свої символи підряд
        if (numberOfMoves[0] + numberOfMoves[1] == board.size * board[0].size) {
            println("Нічия. Гравці $p1, $p2 мають зіграти повторно")
            startPlay(p1, p2)
        }
    }
}

/**
 * Перевірка клітинок на наявність комбінації.
 * Комбінація - 3 символи у горизонтальний/вертикальний ряд або у діагональ
 * @return true якщо комбінацію знайдено
 */
fun hasWinner(board: Array<Array<String>>): Boolean {
    //перевірити рядки
    for (row in board) {
        if (row[0] == row[1] && row[1] == row[2]) {
            return true
        }
    }

    //перевірити колонки
    for (col in 0..2) {
        if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
            return true
        }
    }

    // перевірити діагоналі
    if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
        return true
    }
    if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
        return true
    }

    return false
}

/**
 * Створити поле і заповнити його значеннями, які відповідають коду клітинки
 * @return двомірний масив з кодами клітинок
 */
fun createBoard(): Array<Array<String>> {
    val board = Array(3) { Array(3) { "" } }
    for (row in board.indices) {
        for (col in board[row].indices) {
            board[row][col] = "${row + 1}" + "${col + 1}"
        }
    }
    return board
}

/**
 * Вивести поточне значення поля.
 */
fun showBoard(board: Array<Array<String>>) {
    for (row in board.indices) {
        for (col in board[row].indices) {
            print("|")
            print(board[row][col])
        }
        println("|")
        println("----------")
    }
}

/**
 * Зробити хід. Приймати від гравця код клітинки та перевіряти на відповідність вимогам.
 * @return Код обраної клітинки
 */
fun makeMove(): String {
    while (true) {
        val userInput = readln()
        //дперевірити довдину коду
        if (userInput.length == 2) {
            //кожна цифра має бути в діапазоні [1;3].
            if (userInput[0].toString().toInt() in 1..3 &&
                userInput[1].toString().toInt() in 1..3
            ) {
                //обрана клітинка
                return "${userInput[0].toString().toInt()}" + "${userInput[1].toString().toInt()}"
            } else {
                println("Ви ввели невалідний номер клітинки, спробуйте знову")
            }
        } else {
            println("Код має складатися з двох цифр")
        }
    }
}

/**
 * Метод приймає масив та повертає рандомне значення з нього
 */
fun getRandomPlayer(players: ArrayList<String>): String {
    val randomizer = Random
    return players[randomizer.nextInt(0, players.size - 1)]
}
