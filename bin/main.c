#include <stdio.h>
#include <dlfcn.h>

int main() {

	void *handle = dlopen("/mnt/e/Non_C_Projects/RCPProjects/Android/libmain.so", RTLD_NOW | RTLD_GLOBAL);
	if (handle == NULL) {
		printf("Failed to open library: %s\n", dlerror());
		return 1;
	}

	void *fn = dlsym(handle, "__and___GlobalInitializerFunction.0");
	if (fn == NULL) {
		printf("Failed to find function: %s\n", dlerror());
		return 1;
	}

	return 0;
}

