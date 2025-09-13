import express from 'express'
import path from 'path'

const app = express()
const port = 5173
const __dirname = path.resolve()
const staticDir = path.join(__dirname, '..', 'dist-standalone')

app.use(express.static(staticDir))

app.get('/', (req, res) => {
  res.sendFile(path.join(staticDir, 'index.html'))
})

app.listen(port, '127.0.0.1', () => {
  console.log(`Standalone frontend server listening at http://127.0.0.1:${port}`)
})
