<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Zelda Mini Quests - Edición Compacta</title>
    <style>
        body { background: #000; color: #fff; font-family: monospace; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; margin: 0; user-select: none; }
        #ui { width: 256px; display: flex; justify-content: space-between; font-weight: bold; background: #111; padding: 6px; border: 2px solid #333; box-sizing: border-box; font-size: 11px; }
        canvas { border: 4px solid #fff; box-shadow: 0 0 15px rgba(255,255,255,0.1); background: #000; }
        #tip { margin-top: 6px; color: #aaa; font-size: 11px; text-shadow: 1px 1px #000; }
    </style>
</head>
<body>
    <div id="ui">
        <div>LVL: <span id="lvl">1</span>/6 (<span id="name" style="color:#5c5;">PRADERA</span>)</div>
        <div>HP: <span id="hp" style="color:#f33;">♥♥♥</span></div>
    </div>
    <canvas id="g" width="256" height="256"></canvas>
    <div id="tip">💡 Pasar nivel rápido: tecla <strong style="color:#fff;">N</strong></div>

    <script>
        const canvas = document.getElementById("g");
        const ctx = canvas.getContext("2d");
        const T = 16; // Tamaño reducido de cada bloque para hacerlo más mini

        let lvl = 0;
        let keys = {};
        let enemies = [];
        let changingLevel = false;

        const player = { cx: 1, cy: 7, x: 16, y: 112, speed: 2, hp: 3, d: 'r', atk: 0 };

        const maps = [
            // NIVEL 1: PRADERA
            [
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,1,0,0,0,0,1,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1],
                [1,0,1,0,0,0,0,1,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1],
                [1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2], 
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2], 
                [1,0,0,0,0,0,0,1,0,0,0,0,1,0,0,1],
                [1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
            ],
            // NIVEL 2: BOSQUE
            [
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                [1,0,0,0,1,0,0,0,0,0,1,0,0,0,0,1],
                [1,0,1,0,1,0,1,1,1,0,1,0,1,1,0,1],
                [1,0,1,0,0,0,1,0,0,0,1,0,0,1,0,1],
                [1,0,1,1,1,0,1,0,1,1,1,1,0,1,0,1],
                [1,0,0,0,0,0,0,0,1,0,0,0,0,1,0,1],
                [1,1,1,1,0,1,1,0,0,0,1,1,1,1,0,1],
                [1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,2],
                [1,0,1,1,0,1,0,1,1,0,1,1,0,0,0,2],
                [1,0,1,0,0,0,0,1,0,0,0,1,0,1,1,1],
                [1,0,1,0,1,1,1,1,0,1,0,1,0,0,0,1],
                [1,0,0,0,1,0,0,0,0,1,0,1,1,1,0,1],
                [1,1,1,0,1,0,1,1,0,1,0,0,0,1,0,1],
                [1,0,0,0,0,0,1,0,0,1,1,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
            ],
            // NIVEL 3: DESIERTO
            [
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,1,1,1,0,0,0,0,0,0,1,1,1,0,1],
                [1,0,1,1,1,0,0,0,0,0,0,1,1,1,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,1,1,1,1,0,0,0,0,0,1],
                [1,0,0,0,0,0,1,1,1,1,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,0,0,0,0,1,1,1,1,0,0,0,0,0,1],
                [1,0,0,0,0,0,1,1,1,1,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,1,1,1,0,0,0,0,0,0,1,1,1,0,1],
                [1,0,1,1,1,0,0,0,0,0,0,1,1,1,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
            ],
            // NIVEL 4: ACANTILADO
            [
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,1,1,1,1,1,1,0,0,0,1,1,1,1,0,1],
                [1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1],
                [1,0,1,1,1,0,1,1,0,1,1,0,1,1,1,1],
                [1,0,1,0,0,0,0,0,0,0,0,0,1,0,0,1],
                [1,0,1,0,1,1,1,0,0,1,1,1,1,0,0,1],
                [1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,1,0,1,1,1,0,0,1,1,1,1,0,0,1],
                [1,0,1,0,0,0,0,0,0,0,0,0,1,0,0,1],
                [1,0,1,1,1,0,1,1,0,1,1,0,1,1,1,1],
                [1,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1],
                [1,1,1,1,1,1,1,0,0,0,1,1,1,1,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
            ],
            // NIVEL 5: MAZMORRA
            [
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,1,0,1,1,1,0,1,1,1,0,1,1,1,0,1],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,1,0,1,1,1,0,1,1,1,0,1,1,1,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,0,0,0,1,0,0,0,1,0,0,0,1,0,0,1],
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
            ],
            // NIVEL 6: CASTILLO
            [
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,1,0,1,0,1,0,1,0,1,0,1,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2],
                [1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,0,1,0,1,0,1,0,1,0,1,0,1,0,0,1],
                [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
                [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
            ]
        ];

        const worlds = [
            { n: "PRADERA", b: "#5a2", w: "#381", m: maps[0], type: "grass", e: [{cx:6,cy:4,t:"octo"}, {cx:10,cy:10,t:"octo"}, {cx:12,cy:5,t:"moblin"}] },
            { n: "BOSQUE", b: "#141", w: "#020", m: maps[1], type: "forest", e: [{cx:5,cy:3,t:"octo"}, {cx:9,cy:11,t:"moblin"}, {cx:12,cy:7,t:"moblin"}] },
            { n: "DESIERTO", b: "#e2b667", w: "#c9933b", m: maps[2], type: "desert", e: [{cx:7,cy:4,t:"octo"}, {cx:11,cy:11,t:"moblin"}] },
            { n: "ACANTILADO", b: "#677", w: "#455", m: maps[3], type: "mountain", e: [{cx:7,cy:3,t:"octo"}, {cx:9,cy:12,t:"moblin"}] },
            { n: "MAZMORRA", b: "#333", w: "#222", m: maps[4], type: "dungeon", e: [{cx:6,cy:3,t:"moblin"}, {cx:10,cy:12,t:"moblin"}] },
            { n: "CASTILLO", b: "#411", w: "#200", m: maps[5], type: "castle", e: [{cx:11,cy:7,t:"ganon", hp:4}] }
        ];

        window.addEventListener("keydown", e => {
            keys[e.code] = true;
            if (e.code === "Space" && !player.atk && player.hp > 0 && lvl !== -1) player.atk = 8;
            
            if (e.key === "n" || e.key === "N") {
                if (lvl === -1) loadLevel(0); 
                else loadLevel(lvl + 1); 
            }

            if (player.hp <= 0 && (e.key === "r" || e.key === "R")) loadLevel(lvl);
        });
        window.addEventListener("keyup", e => keys[e.code] = false);

        function loadLevel(i) {
            if (i >= worlds.length) { lvl = -1; return; }
            lvl = i; 
            changingLevel = false;
            player.cx = 1; player.cy = 7; 
            player.x = 16; player.y = 112; // Ajustado a escala de 16
            player.d = 'r'; player.hp = 3;
            
            document.getElementById("lvl").innerText = lvl + 1;
            document.getElementById("name").innerText = worlds[lvl].n;
            document.getElementById("hp").innerText = "♥♥♥";
            
            enemies = worlds[lvl].e.map((e, idx) => {
                let dirs = [{x:1,y:0}, {x:-1,y:0}, {x:0,y:1}, {x:0,y:-1}];
                let d = dirs[idx % dirs.length];
                return {
                    cx: e.cx, cy: e.cy,
                    x: e.cx * T, y: e.cy * T,
                    type: e.t, hp: e.hp || 1,
                    dx: d.x, dy: d.y,
                    targetX: e.cx * T, targetY: e.cy * T
                };
            });
        }

        function isWall(cx, cy) {
            if (cx < 0 || cx >= 16 || cy < 0 || cy >= 16) return true;
            if (worlds[lvl].m[cy][cx] === 2) return false; 
            return worlds[lvl].m[cy][cx] === 1;
        }

        function update() {
            if (player.hp <= 0 || lvl === -1) return;

            if (player.x === player.cx * T && player.y === player.cy * T) {
                if (keys["ArrowUp"] || keys["KeyW"]) { player.d = 'u'; if (!isWall(player.cx, player.cy - 1)) player.cy--; }
                else if (keys["ArrowDown"] || keys["KeyS"]) { player.d = 'd'; if (!isWall(player.cx, player.cy + 1)) player.cy++; }
                else if (keys["ArrowLeft"] || keys["KeyA"]) { player.d = 'l'; if (!isWall(player.cx - 1, player.cy)) player.cx--; }
                else if (keys["ArrowRight"] || keys["KeyD"]) { player.d = 'r'; if (!isWall(player.cx + 1, player.cy)) player.cx++; }
            }

            if (player.x < player.cx * T) player.x += player.speed;
            if (player.x > player.cx * T) player.x -= player.speed;
            if (player.y < player.cy * T) player.y += player.speed;
            if (player.y > player.cy * T) player.y -= player.speed;

            let sw = null;
            if (player.atk) {
                player.atk--; let r = 12;
                if (player.d === 'u') sw = { x: player.x + 2, y: player.y - r, w: 12, h: r };
                if (player.d === 'd') sw = { x: player.x + 2, y: player.y + 16, w: 12, h: r };
                if (player.d === 'l') sw = { x: player.x - r, y: player.y + 2, w: r, h: 12 }; 
                if (player.d === 'r') sw = { x: player.x + 16, y: player.y + 2, w: r, h: 12 };
            }

            enemies.forEach((e, i) => {
                if (e.x === e.targetX && e.y === e.targetY) {
                    e.cx = Math.round(e.x / T);
                    e.cy = Math.round(e.y / T);

                    if (isWall(e.cx + e.dx, e.cy + e.dy) || Math.random() < 0.15) {
                        let dirs = [{x:1,y:0}, {x:-1,y:0}, {x:0,y:1}, {x:0,y:-1}];
                        let valid = dirs.filter(d => !isWall(e.cx + d.x, e.cy + d.y) && worlds[lvl].m[e.cy + d.y][e.cx + d.x] !== 2);
                        if (valid.length > 0) {
                            let chosen = valid[Math.floor(Math.random() * valid.length)];
                            e.dx = chosen.x; e.dy = chosen.y;
                        }
                    }
                    e.targetX = (e.cx + e.dx) * T;
                    e.targetY = (e.cy + e.dy) * T;
                }

                if (e.x < e.targetX) e.x += 1;
                if (e.x > e.targetX) e.x -= 1;
                if (e.y < e.targetY) e.y += 1;
                if (e.y > e.targetY) e.y -= 1;

                if (sw && sw.x < e.x + 14 && sw.x + sw.w > e.x && sw.y < e.y + 14 && sw.y + sw.h > e.y) {
                    e.hp--; player.atk = 0;
                    if (e.hp <= 0) enemies.splice(i, 1);
                }

                if (Math.round(e.x/T) === player.cx && Math.round(e.y/T) === player.cy) {
                    player.hp--;
                    document.getElementById("hp").innerText = "♥".repeat(Math.max(0, player.hp));
                    player.cx = 1; player.cy = 7;
                    player.x = 16; player.y = 112;
                }
            });

            if (enemies.length === 0 && !changingLevel) {
                changingLevel = true;
                setTimeout(() => { loadLevel(lvl + 1); }, 400); 
            }
        }

        function drawObstacle(type, x, y) {
            if (type === "grass") {
                ctx.fillStyle = "#841"; ctx.fillRect(x + 6, y + 10, 4, 6); 
                ctx.fillStyle = "#292"; ctx.beginPath(); ctx.arc(x + 8, y + 6, 6, 0, Math.PI * 2); ctx.fill(); 
            } else if (type === "forest") {
                ctx.fillStyle = "#521"; ctx.fillRect(x + 7, y + 11, 2, 5);
                ctx.fillStyle = "#051"; ctx.beginPath();
                ctx.moveTo(x + 8, y + 1); ctx.lineTo(x + 2, y + 11); ctx.lineTo(x + 14, y + 11); ctx.fill();
            } else if (type === "desert") {
                ctx.fillStyle = "#272";
                ctx.fillRect(x + 7, y + 2, 3, 14); 
                ctx.fillRect(x + 4, y + 6, 4, 2);  
                ctx.fillRect(x + 10, y + 9, 4, 2); 
            } else if (type === "mountain") {
                ctx.fillStyle = "#555"; ctx.beginPath();
                ctx.moveTo(x + 8, y + 1); ctx.lineTo(x + 1, y + 15); ctx.lineTo(x + 15, y + 15); ctx.fill();
                ctx.fillStyle = "#777"; ctx.beginPath();
                ctx.moveTo(x + 8, y + 1); ctx.lineTo(x + 5, y + 15); ctx.lineTo(x + 15, y + 15); ctx.fill();
            } else if (type === "dungeon") {
                ctx.fillStyle = "#222"; ctx.fillRect(x, y, T, T);
                ctx.fillStyle = "#444"; ctx.fillRect(x + 1, y + 1, T - 2, T - 2);
                ctx.fillStyle = "#111"; ctx.fillRect(x + 1, y + 7, T - 2, 2);
            } else if (type === "castle") {
                ctx.fillStyle = "#556"; ctx.fillRect(x + 3, y, 10, T);
                ctx.fillStyle = "#ff0"; ctx.fillRect(x + 6, y + 4, 4, 4); 
            }
        }

        function draw() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            
            if (lvl === -1) {
                ctx.fillStyle = "#000"; ctx.fillRect(0,0,256,256);
                ctx.fillStyle = "#5f5"; ctx.font = "bold 13px monospace"; ctx.textAlign = "center";
                ctx.fillText("¡HAS GANADO!", 128, 110);
                ctx.fillStyle = "#fff"; ctx.font = "11px monospace";
                ctx.fillText("PULSA N PARA VOLVER A JUGAR", 128, 145);
                ctx.textAlign = "start";
                return;
            }

            let w = worlds[lvl];
            ctx.fillStyle = w.b;
            ctx.fillRect(0, 0, canvas.width, canvas.height);

            ctx.fillStyle = "rgba(0,0,0,0.04)";
            for(let i=0; i<16; i+=2) {
                for(let j=0; j<16; j+=2) {
                    if(w.m[j][i] === 0) ctx.fillRect(i*T + 3, j*T + 3, 2, 2);
                }
            }

            for (let r = 0; r < w.m.length; r++) {
                for (let c = 0; c < w.m[r].length; c++) {
                    if (w.m[r][c] === 1) {
                        drawObstacle(w.type, c * T, r * T);
                    } else if (w.m[r][c] === 2) {
                        ctx.fillStyle = "#fff"; ctx.fillRect(c * T, r * T, T, T);
                        ctx.fillStyle = "#0af"; ctx.fillRect(c * T + 3, r * T + 3, T - 6, T - 6);
                    }
                }
            }

            enemies.forEach(e => {
                if (e.type === "octo") {
                    ctx.fillStyle = "#f44"; ctx.fillRect(e.x + 2, e.y + 2, 12, 12);
                    ctx.fillStyle = "#fff"; ctx.fillRect(e.x + 5, e.y + 5, 5, 5);
                } else if (e.type === "moblin") {
                    ctx.fillStyle = "#d82"; ctx.fillRect(e.x + 2, e.y + 2, 12, 12);
                    ctx.fillStyle = "#fff"; ctx.fillRect(e.x + 3, e.y + 6, 3, 3);
                } else if (e.type === "ganon") {
                    ctx.fillStyle = "#717"; ctx.fillRect(e.x + 1, e.y + 1, 14, 14);
                    ctx.fillStyle = "#f0f"; ctx.fillRect(e.x + 5, e.y + 5, 6, 6);
                }
            });

            if (player.hp > 0) {
                ctx.fillStyle = "#0a0"; ctx.fillRect(player.x + 2, player.y + 2, 12, 12);
                ctx.fillStyle = "#fa9"; ctx.fillRect(player.x + 5, player.y + 5, 6, 6);

                if (player.atk) {
                    ctx.fillStyle = "#fff";
                    if (player.d === 'u') ctx.fillRect(player.x + 6, player.y - 5, 3, 6);
                    if (player.d === 'd') ctx.fillRect(player.x + 6, player.y + 15, 3, 6);
                    if (player.d === 'l') ctx.fillRect(player.x - 5, player.y + 6, 6, 3); 
                    if (player.d === 'r') ctx.fillRect(player.x + 15, player.y + 6, 6, 3);
                }
            }

            if (player.hp <= 0) {
                ctx.fillStyle = "rgba(0,0,0,0.8)"; ctx.fillRect(0, 0, 256, 256);
                ctx.fillStyle = "#f33"; ctx.font = "bold 13px monospace"; ctx.textAlign = "center";
                ctx.fillText("HAS CAÍDO", 128, 110);
                ctx.fillStyle = "#fff"; ctx.font = "11px monospace";
                ctx.fillText("PULSA R PARA REINTENTAR", 128, 145);
                ctx.textAlign = "start"; 
            }
        }

        function loop() { update(); draw(); requestAnimationFrame(loop); }
        loadLevel(0);
        loop();
    </script>
</body>
</html>
