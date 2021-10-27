# Інструкція
## Формат файлу конфігурації
Конфігураційний файл має знаходитись поряд з файлом програми і повинен мати назву `config.txt`.
На початку у ньому знаходиться список опіцій, а далі йде опис смуг.

### Cписок опцій
Задається у виді `%опція%=%значення%`. Кожна опція - на окремому рядку. Порожні рядки та пробіли допускаються.
Наступні опції наявні:
* `image` - шлях до файлу з зображенням, що буде відображатися
* `n` - кількість смуг
* `back` - шлях до файлу з зображенням, що буде знаходитись на задньому плані
* `minDelta` - мінімальна затримка між смугами. Якщо явно зазначена затримка для смуги буде меншою -
їй буде присвоєне значення `minDelta`
* `fullTime` - час для виводу усього зображення. Використовується у разі відсутності описів смуг.

### Описи смуг

Кожна смуга - окремий рядок, що має вигляд `i:d` або `i:d:t`. 
* `i` - індекс смуги (індекси починаються з нуля)
* `d` - затримка, що повинна бути після смуги, у секундах
* `t` - опційний параметр, що дозволяє задати прозорість смуги.

Порядок відображення смуг задається їх порядком у файлі.

Якщо якісь смуги не були описані, то ім задається поведінка за замовчуванням - порядок відображення згори, 
час для відображення повної картинки - 6 секунд (можна перевизначити за допомогою опції `fullTime`).

### Зразок конфігураційного файлу

```
image = example.png
back=space_sky.jpg
n=32

fullTime = 3

15 : 160 : 0.1
0 : 10 : 0.1
14 : 150 : 0.2
1 : 20 : 0.2
13 : 140 : 0.3
2 : 30 : 0.3
12 : 130 : 0.4
3 : 40 : 0.4
11 : 120 : 0.5
4 : 50 : 0.5
10 : 110 : 0.6
5 : 60 : 0.6
9 : 100 : 0.7
6:70:0.7
8:90
7:80
```

### Управління програмою

Після запуску програми - картинка починає відображуватись зразу. 
Для того, щоб повторити анімацію - треба натиснути пробіл. Для того, щоб очистити поле - треба натиснути `c`.
