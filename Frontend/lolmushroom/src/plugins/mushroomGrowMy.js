import Phaser from 'phaser'
import smallGood from '@/assets/mushroomGrow/SmallGood.svg'
import smallHurt from '@/assets/mushroomGrow/SmallHurt.svg'
import smallNormal from '@/assets/mushroomGrow/SmallNormal.svg'
import midGood from '@/assets/mushroomGrow/MidGood.svg'
import midHurt from '@/assets/mushroomGrow/MidHurt.svg'
import midNormal from '@/assets/mushroomGrow/MidNormal.svg'
import bigGood from '@/assets/mushroomGrow/BigGood.svg'
import bigHurt from '@/assets/mushroomGrow/BigHurt.svg'
import bigNormal from '@/assets/mushroomGrow/BigNormal.svg'

class MushroomGrowGame extends Phaser.Scene {
  constructor() {
    super('MushroomGrowGame') // 'MushroomGrowGame'이라는 키를 통해 이 장면을 식별합니다.
  }

  preload() {
    this.load.image('smallGood', smallGood)
    this.load.image('smallHurt', smallHurt)
    this.load.image('smallNormal', smallNormal)
    this.load.image('midGood', midGood)
    this.load.image('midHurt', midHurt)
    this.load.image('midNormal', midNormal)
    this.load.image('bigGood', bigGood)
    this.load.image('bigHurt', bigHurt)
    this.load.image('bigNormal', bigNormal)
  }

  create() {
    this.mushroomSize = 20 // 초기 크기를 더 크게 설정
    this.mushroomGrowthAmount = 20 // 버섯 크기 변경 단위
    this.mushroomState = 'Normal' // Normal, Good, Hurt
    this.mushroomSprite = this.add.sprite(400, 300, this.getMushroomKey())

    this.growButton = this.add
      .text(100, 50, 'Grow', { fontSize: '32px', fill: '#0f0' })
      .setInteractive()
    this.attackButton = this.add
      .text(200, 50, 'Attack', { fontSize: '32px', fill: '#f00' })
      .setInteractive()

    this.growButton.on('pointerdown', () => {
      this.growMushroom()
    })

    this.attackButton.on('pointerdown', () => {
      this.attackMushroom()
    })
  }

  getMushroomKey() {
    let size = 'small'
    if (this.mushroomSize >= 200) {
      size = 'big'
    } else if (this.mushroomSize >= 100) {
      size = 'mid'
    }
    return `${size}${this.mushroomState}`
  }

  updateMushroomSprite() {
    this.mushroomSprite.setTexture(this.getMushroomKey())
    this.mushroomSprite.setDisplaySize(this.mushroomSize, this.mushroomSize)
  }

  growMushroom() {
    if (this.mushroomSize < 150) {
      // 최대 크기를 조정
      this.tweens.add({
        targets: this.mushroomSprite,
        displayWidth: this.mushroomSize + this.mushroomGrowthAmount,
        displayHeight: this.mushroomSize + this.mushroomGrowthAmount,
        duration: 1000,
        onUpdate: () => {
          this.mushroomSize = this.mushroomSprite.displayWidth
          this.mushroomState = 'Good'
          this.updateMushroomSprite()
        },
        onComplete: () => {
          this.mushroomState = 'Normal'
          this.updateMushroomSprite()
        }
      })
    }
  }

  attackMushroom() {
    if (this.mushroomSize > 60) {
      // 최소 크기를 조정
      this.tweens.add({
        targets: this.mushroomSprite,
        displayWidth: this.mushroomSize - this.mushroomGrowthAmount,
        displayHeight: this.mushroomSize - this.mushroomGrowthAmount,
        duration: 1000,
        onUpdate: () => {
          this.mushroomSize = this.mushroomSprite.displayWidth
          this.mushroomState = 'Hurt'
          this.updateMushroomSprite()
        },
        onComplete: () => {
          this.mushroomState = 'Normal'
          this.updateMushroomSprite()
        }
      })
    }
  }
}

const config = {
  type: Phaser.AUTO,
  pixelArt: true,
  width: 800,
  height: 600,
  scene: MushroomGrowGame
}

let game

function initGame(parentElement) {
  config.parent = parentElement
  game = new Phaser.Game(config)
}

function destroyGame() {
  if (game) {
    game.destroy(true)
  }
}

export { initGame, destroyGame }
