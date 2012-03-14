Insertion Ordered Hash ----------
%(a => 'apple', b => 'boy', c => 'cat')
%(a => 'apple', b => 'boy', c => 'cat', d => 'dog')
Access 'a' apple
%(a => 'apple', b => 'boy', c => 'cat', d => 'dog')
%(b => 'boy', c => 'cat', d => 'dog', e => 'emu')
%(c => 'cat', d => 'dog', e => 'emu', m => 'night elf mohawk')
Remove 'm'
%(c => 'cat', d => 'dog', e => 'emu')
%(c => 'cat', d => 'dog', e => 'emu', n => 'nerf')
%(d => 'dog', e => 'emu', n => 'nerf', x => 'XXX')
Access Ordered Hash    ----------
%(a => 'apple', b => 'boy', c => 'cat')
%(a => 'apple', b => 'boy', c => 'cat', d => 'dog')
Access 'a' apple
%(b => 'boy', c => 'cat', d => 'dog', a => 'apple')
%(c => 'cat', d => 'dog', a => 'apple', e => 'emu')
%(d => 'dog', a => 'apple', e => 'emu', m => 'night elf mohawk')
Remove 'm'
%(d => 'dog', a => 'apple', e => 'emu')
%(d => 'dog', a => 'apple', e => 'emu', n => 'nerf')
%(a => 'apple', e => 'emu', n => 'nerf', x => 'XXX')
