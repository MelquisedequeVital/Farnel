(function () {
	// Carrossel de pratos do carrinho
	const track = document.getElementById('dish-track');
	document.getElementById('scroll-prev')?.addEventListener('click', () => {
		track.scrollBy({ left: -300, behavior: 'smooth' });
	});
	document.getElementById('scroll-next')?.addEventListener('click', () => {
		track.scrollBy({ left: 300, behavior: 'smooth' });
	});

	// Copiar o código PIX de exemplo
	const copiar = document.getElementById('copiar-pix');
	const codigo = document.getElementById('pix-codigo');
	copiar?.addEventListener('click', () => {
		navigator.clipboard.writeText(codigo.value).then(() => {
			copiar.textContent = 'Copiado';
			setTimeout(() => copiar.textContent = 'Copiar', 2500);
		});
	});
})();
