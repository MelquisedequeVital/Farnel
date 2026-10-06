(function () {
	// Pré-visualização e checagem de tamanho da foto antes do envio
	const LIMITE_BYTES = 5 * 1024 * 1024;
	const input = document.getElementById('foto');
	const preview = document.getElementById('foto-preview');
	const vazia = document.getElementById('foto-vazia');
	const rotulo = document.getElementById('foto-rotulo');
	const erro = document.getElementById('foto-erro');
	const remover = document.getElementById('removerFoto1');

	input.addEventListener('change', () => {
		const arquivo = input.files[0];
		erro.classList.add('hidden');
		if (!arquivo) {
			return;
		}
		if (arquivo.size > LIMITE_BYTES) {
			input.value = '';
			erro.textContent = 'A foto deve ter no máximo 5 MB.';
			erro.classList.remove('hidden');
			return;
		}
		preview.src = URL.createObjectURL(arquivo);
		preview.classList.remove('hidden');
		vazia.classList.add('hidden');
		rotulo.textContent = arquivo.name;
		if (remover) {
			remover.checked = false;
		}
	});
})();
