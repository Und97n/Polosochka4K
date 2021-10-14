# Config file structure
* First row: `n`, where `n` is number of stripes
* Next `n` rows: `pos:ms`. <br>
  Each line `i` corresponds to `i`'th stripe of image, where `pos` denotes order of current stripe, and `ms` denotes number of milliseconds, for which this stripe will be printing on screen. Basically, `ms` is delay before next stripe, can't be less than `16.7` milliseconds. <br>
#### No empty lines (including last newline in file) are accepted!