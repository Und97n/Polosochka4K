# Config file structure
* First row: path to the image
* Second row: `n`, where `n` is number of stripes
* Rest rows: `pos:ms` or `pos:ms:trp`. <br>
  Each line `i` corresponds to `i`'th stripe of image, where `pos` denotes order of current stripe, and `ms` denotes number of milliseconds, for which this stripe will be printing on screen. Basically, `ms` is delay before next stripe, can't be less than `16.7` milliseconds. `trp` means for `transparrency` - from zero to one (zero means fully transparent, one means normal image)<br>

NOTE:
* Order of rows is an order of drawing
* Empty lines are ignored
* If some stripes are missing in config - they are generated using common method: top-down order of drawing, `6000/n` delay between stripes.