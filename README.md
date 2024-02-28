# team-8
### Skriv inn navn og uio-brukernavn:
Ørjan Hammer Sylta (orjanhsy)\
Julie Alme :)) (julikal)\
Muna Isman (Munaai)\
Agnes (agneshb)\
Evelyn (evelynsb)

## Git
[Cheat sheet](https://education.github.com/git-cheat-sheet-education.pdf)

### Regler for pull-request
Dette ser vi på vi sammen.

### Vi vil som oftest bruke git som dette

1. `git branch` -> viser frem tilgjengelige brancher og hvilken du er på.
   * `git branch <branch-name>` -> oppretter en ny branch.
   * `git checkout <branch/commit>` -> "besøk" en branch/commit.

2. `git pull` -> henter staten til upstream branch og auto-merger (kan oppstå konflikter).
   * `git fetch` -> henter staten til upstream uten å auto-merge.

3. `git status` -> viser modifiseringer.

4. `git add <fil/mappe>` -> legger filer til 'staging area'.
   * `git reset <file>` -> fjerner en fil fra staging area.

5. `git commit -m "message"` -> lagrer en 'snapshot' av det som ligger i staging area.
   * `git commit --amend -m "new message"` -> oppdater siste commit-message. Gjør kun om du ikke har pushet forrige commit.
   * `git add, git commit --amend -m "new message"` -> oppdaterer siste commit. Gjør kun om du ikke har pushet siste commit.

6. `git push` -> laster opp commits fra staging area til en gitt branch (default er upstream branch).
   * `git push -u <upstream-branch>` -> Oppreter en remote branch tilsvarende den du er i og pusher til den.  
     Gjør dette første gang du pusher fra en ny branch.


### Håndtering av merge conlicts
kommer

### Generelt nyttig
`git diff` -> viser forskjellen mellom states. Default er forskjellen på current working directory til siste commit.
* `git diff <commit1> <commit>`
* `git diff <branch1> <branch2>`
* `git diff <commit>` -> viser forskjellen mellom current state og en commit.

`git log` -> se commithistorien i branchen du er i.
* `git log --oneline` -> mer lesbart.
* `git log --graph --oneline --decorate` -> grafrepresentasjon av loggen.

`git stash` -> fjerner og lagrer endringene dine i et 'stash'.
* `git stash pop` -> henter endringene fra stash
* `git stash save <stash-name>` -> oppretter en ny stash.
* `git stash list` -> se stash du har opprettet.
* `git stash apply <index>` -> henter endringene fra stash med index &lt;index&gt;.

`git merge <branch>` -> sammenfletter branchen du er i med &lt;branch&gt; som en ny commit.
* `git merge --squash <branch>` -> stager en merge med &lt;branch&gt;. Koker commitsene i &lt;branch&gt; ned til 1 ny commit uten å ødelegge de som ligger i &lt;branch&gt;.

`git revert -m <commit>` -> oppretter en ny commit hvor det som ble endret i &lt;commit&gt; er fjernet. &lt;commit&gt; vil forstsatt finnes i log.

