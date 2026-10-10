(function () {
	// Carrossel principal
	const slider = document.getElementById('hero-slider');
	const indicator = document.getElementById('carousel-indicator');
	const totalSlides = slider ? slider.children.length : 0;
	let currentSlide = 0;

	function updateSlide(index) {
		currentSlide = (index + totalSlides) % totalSlides;
		slider.style.transform = `translateX(-${currentSlide * 100}%)`;
		indicator.textContent = `0${currentSlide + 1} / 0${totalSlides}`;
	}

	if (totalSlides > 1) {
		document.getElementById('prev-slide').addEventListener('click', () => updateSlide(currentSlide - 1));
		document.getElementById('next-slide').addEventListener('click', () => updateSlide(currentSlide + 1));
	}

	// Faixa de categorias
	const catTrack = document.getElementById('category-track');
	document.getElementById('cat-scroll-left')?.addEventListener('click', () => {
		catTrack.scrollBy({ left: -340, behavior: 'smooth' });
	});
	document.getElementById('cat-scroll-right')?.addEventListener('click', () => {
		catTrack.scrollBy({ left: 340, behavior: 'smooth' });
	});
})();
